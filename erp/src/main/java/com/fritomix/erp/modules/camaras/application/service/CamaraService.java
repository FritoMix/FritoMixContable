package com.fritomix.erp.modules.camaras.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.exception.BusinessException;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.camaras.application.dto.request.*;
import com.fritomix.erp.modules.camaras.application.dto.response.CamaraHistorialResponse;
import com.fritomix.erp.modules.camaras.application.dto.response.CamaraVerificacionResponse;
import com.fritomix.erp.modules.camaras.application.mapper.CamaraMapper;
import com.fritomix.erp.modules.camaras.domain.entity.CamaraHistorial;
import com.fritomix.erp.modules.camaras.domain.entity.CamaraVerificacion;
import com.fritomix.erp.modules.camaras.domain.entity.CamaraVerificacionDetalle;
import com.fritomix.erp.modules.camaras.domain.repository.CamaraHistorialRepository;
import com.fritomix.erp.modules.camaras.domain.repository.CamaraVerificacionRepository;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchDetailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CamaraService {

    private final CamaraVerificacionRepository camaraRepository;
    private final CamaraHistorialRepository historialRepository;
    private final CamaraMapper camaraMapper;
    private final ObjectMapper objectMapper;
    private final DispatchDetailRepository dispatchDetailRepository;

    /**
     * Crea o reinicializa una verificación de cámaras para un despacho.
     */
    @Transactional
    public CamaraVerificacion crearVerificacionParaDispatch(Dispatch dispatch, JwtUserInfo currentUser) {
        log.info("Creando/Obteniendo Verificación de Cámaras para Dispatch ID={}", dispatch.getId());

        Optional<CamaraVerificacion> existente = camaraRepository.findByDispatchId(dispatch.getId());
        if (existente.isPresent()) {
            CamaraVerificacion v = existente.get();
            // Si la verificación ya existe pero no tiene detalles, sincronizarlos ahora
            int nuevos = syncDetalles(v, dispatch);
            if (nuevos > 0) {
                log.info("Sincronizados {} detalles faltantes en verificación ID={} para dispatch ID={}",
                        nuevos, v.getId(), dispatch.getId());
                v = camaraRepository.save(v);
            }
            return v;
        }

        CamaraVerificacion v = CamaraVerificacion.builder()
                .dispatch(dispatch)
                .estadoCamaras(CamaraFlow.PENDIENTE_CAMARAS)
                .detalles(new ArrayList<>())
                .build();

        if (dispatch.getDetails() != null) {
            for (DispatchDetail dd : dispatch.getDetails()) {
                CamaraVerificacionDetalle det = CamaraVerificacionDetalle.builder()
                        .verificacion(v)
                        .dispatchDetail(dd)
                        .cantidadDespachada(dd.getQuantity() != null ? dd.getQuantity() : BigDecimal.ZERO)
                        .cantidadVerificada(null)
                        .estadoLinea(CamaraFlow.LINEA_PENDIENTE)
                        .build();
                v.getDetalles().add(det);
            }
        }

        CamaraVerificacion guardada = camaraRepository.save(v);

        registrarHistorial(
                guardada,
                CamaraFlow.EVENTO_CREADO,
                currentUser != null ? currentUser.userId() : null,
                currentUser != null ? (currentUser.firstName() + " " + currentUser.lastName()).trim() : "SISTEMA",
                null,
                CamaraFlow.PENDIENTE_CAMARAS,
                "Ficha de Cámaras iniciada/creada para despacho " + dispatch.getDispatchNumber(),
                Map.of("dispatchNumber", dispatch.getDispatchNumber(), "detallesCount", v.getDetalles().size())
        );

        return guardada;
    }

    /**
     * Consulta paginada de la bandeja de cámaras con filtros opcionales.
     */
    @Transactional(readOnly = true)
    public PageResponse<CamaraVerificacionResponse> getFichas(
            String estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            String clienteNombre,
            Pageable pageable) {

        List<String> estados = estado != null && !estado.isBlank() ? List.of(estado) : null;
        Page<Long> idsPage = camaraRepository.findIds(clienteNombre, estados, fechaInicio, fechaFin, pageable);

        if (idsPage.isEmpty()) {
            return PageResponse.of(Collections.emptyList(), idsPage.getNumber(), idsPage.getSize(), idsPage.getTotalElements(), idsPage.getTotalPages());
        }

        List<CamaraVerificacion> verificaciones = camaraRepository.findAllWithFetchByIds(idsPage.getContent());
        List<CamaraVerificacionResponse> content = verificaciones.stream()
                .map(camaraMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(content, idsPage.getNumber(), idsPage.getSize(), idsPage.getTotalElements(), idsPage.getTotalPages());
    }

    /**
     * Obtiene el detalle completo de una ficha de verificación por su ID.
     */
    @Transactional
    public CamaraVerificacionResponse getFichaById(Long id) {
        CamaraVerificacion v = findByIdOrThrow(id);
        // Sincronizar detalles en caso de que el dispatch haya recibido productos después
        // de la creación inicial de la verificación (estado aún modificable)
        if (!CamaraFlow.isEstadoCerrado(v.getEstadoCamaras())) {
            List<DispatchDetail> dispatchDetails = dispatchDetailRepository
                    .findByDispatchId(v.getDispatch().getId());
            int nuevos = syncDetallesDesdeList(v, dispatchDetails);
            if (nuevos > 0) {
                log.info("getFichaById: sincronizados {} detalles en verificación ID={}", nuevos, id);
                v = camaraRepository.save(v);
                // Recargar con fetch completo para que el mapper tenga datos de producto
                v = findByIdOrThrow(id);
            }
        }
        return camaraMapper.toResponse(v);
    }

    /**
     * Inicia la verificación física por parte del personal de cámaras (de PENDIENTE_CAMARAS a EN_VERIFICACION).
     */
    @Transactional
    public CamaraVerificacionResponse iniciarVerificacion(Long dispatchId, JwtUserInfo currentUser) {
        CamaraVerificacion v = camaraRepository.findByDispatchId(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe verificación de cámaras para el despacho ID: " + dispatchId));

        String estadoAnterior = v.getEstadoCamaras();
        CamaraFlow.validateTransition(estadoAnterior, CamaraFlow.EN_VERIFICACION);

        v.setEstadoCamaras(CamaraFlow.EN_VERIFICACION);
        v.setVerificadoPorUserId(currentUser.userId());
        v.setFechaInicioVerificacion(LocalDateTime.now());

        CamaraVerificacion guardada = camaraRepository.save(v);

        registrarHistorial(
                guardada,
                CamaraFlow.EVENTO_INICIADO,
                currentUser.userId(),
                getUserFullName(currentUser),
                estadoAnterior,
                CamaraFlow.EN_VERIFICACION,
                "Inicio de verificación física en cámaras",
                null
        );

        return camaraMapper.toResponse(guardada);
    }

    /**
     * Registra las cantidades físicas verificadas por línea.
     */
    @Transactional
    public CamaraVerificacionResponse registrarVerificacionCantidades(
            Long id,
            CamaraVerificarRequest request,
            JwtUserInfo currentUser) {

        CamaraVerificacion v = findByIdOrThrow(id);
        if (CamaraFlow.isEstadoCerrado(v.getEstadoCamaras())) {
            throw new BusinessException("La ficha ya se encuentra APROBADA y no permite modificaciones.");
        }

        Map<Long, CamaraVerificacionDetalle> detallesMap = v.getDetalles().stream()
                .collect(Collectors.toMap(CamaraVerificacionDetalle::getId, d -> d));

        boolean hayDiferencias = false;

        for (CamaraVerificarRequest.LineaVerificadaRequest lvr : request.lineas()) {
            CamaraVerificacionDetalle det = detallesMap.get(lvr.detalleId());
            if (det == null) {
                throw new BusinessException("El detalle ID " + lvr.detalleId() + " no pertenece a la verificación ID " + id);
            }

            BigDecimal despachada = det.getCantidadDespachada();
            BigDecimal verificada = lvr.cantidadVerificada();

            det.setCantidadVerificada(verificada);
            det.setTipoInconsistencia(lvr.tipoInconsistencia());
            det.setObservacionLinea(lvr.observacionLinea());

            if (verificada != null && verificada.compareTo(despachada) == 0) {
                det.setEstadoLinea(CamaraFlow.LINEA_OK);
            } else {
                det.setEstadoLinea(CamaraFlow.LINEA_DIFERENCIA);
                hayDiferencias = true;
            }
        }

        if (request.observacionesGenerales() != null) {
            v.setObservacionesGenerales(request.observacionesGenerales());
        }

        CamaraVerificacion guardada = camaraRepository.save(v);

        registrarHistorial(
                guardada,
                CamaraFlow.EVENTO_VERIFICADO,
                currentUser.userId(),
                getUserFullName(currentUser),
                v.getEstadoCamaras(),
                v.getEstadoCamaras(),
                "Registro de cantidades físicas verificadas. Hay diferencias: " + hayDiferencias,
                Map.of("lineasVerificadas", request.lineas().size(), "hayDiferencias", hayDiferencias)
        );

        return camaraMapper.toResponse(guardada);
    }

    /**
     * Pone la ficha en estado OBSERVADA indicando discrepancias o inconsistencias.
     */
    @Transactional
    public CamaraVerificacionResponse observarFicha(
            Long id,
            CamaraObservarRequest request,
            JwtUserInfo currentUser) {

        CamaraVerificacion v = findByIdOrThrow(id);
        String estadoAnterior = v.getEstadoCamaras();

        CamaraFlow.validateTransition(estadoAnterior, CamaraFlow.OBSERVADA);

        v.setEstadoCamaras(CamaraFlow.OBSERVADA);
        v.setObservacionesGenerales(request.observacion());

        CamaraVerificacion guardada = camaraRepository.save(v);

        registrarHistorial(
                guardada,
                CamaraFlow.EVENTO_OBSERVADO,
                currentUser.userId(),
                getUserFullName(currentUser),
                estadoAnterior,
                CamaraFlow.OBSERVADA,
                request.observacion(),
                null
        );

        return camaraMapper.toResponse(guardada);
    }

    /**
     * Aprueba formalmente la ficha de verificación.
     */
    @Transactional
    public CamaraVerificacionResponse aprobarFicha(
            Long id,
            CamaraAprobarRequest request,
            JwtUserInfo currentUser) {

        CamaraVerificacion v = findByIdOrThrow(id);
        String estadoAnterior = v.getEstadoCamaras();

        CamaraFlow.validateTransition(estadoAnterior, CamaraFlow.APROBADA);

        boolean isAdmin = currentUser.role() != null &&
                (currentUser.role().equalsIgnoreCase("ADMIN") || currentUser.role().equalsIgnoreCase("ROLE_ADMIN"));

        // Validaciones de líneas
        for (CamaraVerificacionDetalle det : v.getDetalles()) {
            if (det.getCantidadVerificada() == null) {
                throw new BusinessException("No se puede aprobar la ficha: la línea '" +
                        (det.getDispatchDetail() != null && det.getDispatchDetail().getProduct() != null
                                ? det.getDispatchDetail().getProduct().getName() : det.getId()) +
                        "' no tiene cantidad verificada registrada.");
            }
            if (CamaraFlow.LINEA_DIFERENCIA.equals(det.getEstadoLinea()) && !isAdmin) {
                throw new BusinessException("No se puede aprobar la ficha: existen líneas con diferencias de cantidad y solo un Administrador puede aprobar con discrepancias.");
            }
        }

        v.setEstadoCamaras(CamaraFlow.APROBADA);
        v.setAprobadoPorUserId(currentUser.userId());
        v.setFechaAprobacion(LocalDateTime.now());
        if (request.confirmacion() != null) {
            v.setObservacionesGenerales(
                    (v.getObservacionesGenerales() != null ? v.getObservacionesGenerales() + "\n" : "") +
                            "[Aprobación]: " + request.confirmacion()
            );
        }

        CamaraVerificacion guardada = camaraRepository.save(v);

        registrarHistorial(
                guardada,
                CamaraFlow.EVENTO_APROBADO,
                currentUser.userId(),
                getUserFullName(currentUser),
                estadoAnterior,
                CamaraFlow.APROBADA,
                request.confirmacion(),
                Map.of("aprobadoPorId", currentUser.userId(), "fechaAprobacion", guardada.getFechaAprobacion().toString())
        );

        return camaraMapper.toResponse(guardada);
    }

    /**
     * Rechaza la ficha de verificación.
     */
    @Transactional
    public CamaraVerificacionResponse rechazarFicha(
            Long id,
            CamaraRechazarRequest request,
            JwtUserInfo currentUser) {

        CamaraVerificacion v = findByIdOrThrow(id);
        String estadoAnterior = v.getEstadoCamaras();

        CamaraFlow.validateTransition(estadoAnterior, CamaraFlow.RECHAZADA);

        v.setEstadoCamaras(CamaraFlow.RECHAZADA);
        v.setObservacionesGenerales(
                (v.getObservacionesGenerales() != null ? v.getObservacionesGenerales() + "\n" : "") +
                        "[RECHAZO]: " + request.motivo()
        );

        CamaraVerificacion guardada = camaraRepository.save(v);

        registrarHistorial(
                guardada,
                CamaraFlow.EVENTO_RECHAZADO,
                currentUser.userId(),
                getUserFullName(currentUser),
                estadoAnterior,
                CamaraFlow.RECHAZADA,
                request.motivo(),
                null
        );

        return camaraMapper.toResponse(guardada);
    }

    /**
     * Reabre una ficha en estado OBSERVADA o RECHAZADA.
     */
    @Transactional
    public CamaraVerificacionResponse reabrirFicha(
            Long id,
            CamaraReabrirRequest request,
            JwtUserInfo currentUser) {

        CamaraVerificacion v = findByIdOrThrow(id);
        String estadoAnterior = v.getEstadoCamaras();

        CamaraFlow.validateTransition(estadoAnterior, CamaraFlow.EN_VERIFICACION);

        v.setEstadoCamaras(CamaraFlow.EN_VERIFICACION);

        CamaraVerificacion guardada = camaraRepository.save(v);

        registrarHistorial(
                guardada,
                CamaraFlow.EVENTO_REABIERTO,
                currentUser.userId(),
                getUserFullName(currentUser),
                estadoAnterior,
                CamaraFlow.EN_VERIFICACION,
                "Ficha reabierta para corrección: " + request.motivo(),
                null
        );

        return camaraMapper.toResponse(guardada);
    }

    /**
     * Obtiene el historial completo de auditoría de una ficha.
     */
    @Transactional(readOnly = true)
    public List<CamaraHistorialResponse> getHistorial(Long id) {
        findByIdOrThrow(id); // validar existencia
        return historialRepository.findByVerificacionIdOrderByCreatedAtAsc(id).stream()
                .map(camaraMapper::toHistorialResponse)
                .collect(Collectors.toList());
    }

    /**
     * Sincroniza los detalles de la verificación de cámaras cuando se añaden
     * productos al despacho después de que la verificación fue creada.
     * Llamado desde DispatchService.update() tras guardar los detalles del despacho.
     */
    @Transactional
    public void sincronizarDetallesConDispatch(Dispatch dispatch) {
        camaraRepository.findByDispatchId(dispatch.getId()).ifPresent(v -> {
            if (CamaraFlow.isEstadoCerrado(v.getEstadoCamaras())) {
                log.debug("Verificación ID={} está cerrada; se omite sincronización", v.getId());
                return;
            }
            int nuevos = syncDetalles(v, dispatch);
            if (nuevos > 0) {
                log.info("sincronizarDetallesConDispatch: {} detalles nuevos en verificación ID={} para dispatch ID={}",
                        nuevos, v.getId(), dispatch.getId());
                camaraRepository.save(v);
            }
        });
    }

    /**
     * Sincroniza los detalles de una CamaraVerificacion con los DispatchDetail del dispatch.
     * Sólo añade las líneas que aún no existen (por dispatch_detail_id).
     * @return número de detalles nuevos añadidos
     */
    private int syncDetalles(CamaraVerificacion v, Dispatch dispatch) {
        if (dispatch.getDetails() == null || dispatch.getDetails().isEmpty()) {
            return 0;
        }
        return syncDetallesDesdeList(v, dispatch.getDetails());
    }

    /**
     * Sincroniza los detalles de una CamaraVerificacion a partir de una lista de DispatchDetail.
     * @return número de detalles nuevos añadidos
     */
    private int syncDetallesDesdeList(CamaraVerificacion v, List<DispatchDetail> dispatchDetails) {
        if (dispatchDetails == null || dispatchDetails.isEmpty()) {
            return 0;
        }
        // IDs de DispatchDetail ya registrados en la verificación
        Set<Long> yaRegistrados = v.getDetalles().stream()
                .map(d -> d.getDispatchDetail() != null ? d.getDispatchDetail().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        int count = 0;
        for (DispatchDetail dd : dispatchDetails) {
            if (!yaRegistrados.contains(dd.getId())) {
                CamaraVerificacionDetalle det = CamaraVerificacionDetalle.builder()
                        .verificacion(v)
                        .dispatchDetail(dd)
                        .cantidadDespachada(dd.getQuantity() != null ? dd.getQuantity() : BigDecimal.ZERO)
                        .cantidadVerificada(null)
                        .estadoLinea(CamaraFlow.LINEA_PENDIENTE)
                        .build();
                v.getDetalles().add(det);
                count++;
            }
        }
        return count;
    }

    private CamaraVerificacion findByIdOrThrow(Long id) {
        return camaraRepository.findByIdWithFetch(id)
                .or(() -> camaraRepository.findById(id))
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la verificación de cámaras con ID: " + id));
    }

    private String getUserFullName(JwtUserInfo user) {
        if (user == null) return "SISTEMA";
        String name = ((user.firstName() != null ? user.firstName() : "") + " " + (user.lastName() != null ? user.lastName() : "")).trim();
        return name.isBlank() ? user.email() : name;
    }

    private void registrarHistorial(
            CamaraVerificacion v,
            String tipoEvento,
            Long userId,
            String nombreUsuario,
            String estadoAnterior,
            String estadoNuevo,
            String observacion,
            Object metadata) {

        String metadataJson = null;
        if (metadata != null) {
            try {
                metadataJson = objectMapper.writeValueAsString(metadata);
            } catch (JsonProcessingException e) {
                log.warn("Error al serializar metadata para historial de cámaras", e);
            }
        }

        CamaraHistorial h = CamaraHistorial.builder()
                .verificacion(v)
                .tipoEvento(tipoEvento)
                .userId(userId)
                .nombreUsuario(nombreUsuario)
                .estadoAnterior(estadoAnterior)
                .estadoNuevo(estadoNuevo)
                .observacion(observacion)
                .metadata(metadataJson)
                .createdAt(LocalDateTime.now())
                .build();

        historialRepository.save(h);
    }
}
