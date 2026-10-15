package com.fritomix.erp.modules.camaras.application.mapper;

import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.camaras.application.dto.response.CamaraHistorialResponse;
import com.fritomix.erp.modules.camaras.application.dto.response.CamaraVerificacionResponse;
import com.fritomix.erp.modules.camaras.application.dto.response.CamaraVerificacionResponse.DetalleVerificacionResponse;
import com.fritomix.erp.modules.camaras.domain.entity.CamaraHistorial;
import com.fritomix.erp.modules.camaras.domain.entity.CamaraVerificacion;
import com.fritomix.erp.modules.camaras.domain.entity.CamaraVerificacionDetalle;
import com.fritomix.erp.modules.camaras.application.service.CamaraFlow;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CamaraMapper {

    private final UserRepository userRepository;

    public CamaraVerificacionResponse toResponse(CamaraVerificacion v) {
        Dispatch d = v.getDispatch();
        java.util.Collection<Order> orders = d.getOrders() != null ? d.getOrders() : Collections.emptyList();

        String clienteNombre = orders.stream()
                .filter(o -> o.getCustomer() != null)
                .map(o -> o.getCustomer().getBusinessName())
                .distinct()
                .collect(Collectors.joining(", "));

        String responsableDespacho = resolveUserName(d.getUserId());
        String verificadoPorNombre = resolveUserName(v.getVerificadoPorUserId());
        String aprobadoPorNombre   = resolveUserName(v.getAprobadoPorUserId());

        String vehiclePlate = StringUtils.hasText(d.getVehiclePlate())
                ? d.getVehiclePlate()
                : (d.getVehicle() != null ? d.getVehicle().getVehicleNumber() : null);

        String driverName = d.getDriver() != null ? d.getDriver().getName() : null;

        List<DetalleVerificacionResponse> detalles = v.getDetalles() == null
                ? Collections.emptyList()
                : v.getDetalles().stream().map(this::toDetalleResponse).collect(Collectors.toList());

        long lineasOk          = detalles.stream().filter(dt -> CamaraFlow.LINEA_OK.equals(dt.estadoLinea())).count();
        long lineasDiferencia  = detalles.stream().filter(dt -> CamaraFlow.LINEA_DIFERENCIA.equals(dt.estadoLinea())).count();
        long lineasPendientes  = detalles.stream().filter(dt -> CamaraFlow.LINEA_PENDIENTE.equals(dt.estadoLinea())).count();

        return CamaraVerificacionResponse.builder()
                .id(v.getId())
                .dispatchId(d.getId())
                .dispatchNumber(d.getDispatchNumber())
                .estadoCamaras(v.getEstadoCamaras())
                .clienteNombre(clienteNombre)
                .responsableDespacho(responsableDespacho)
                .fechaDespacho(d.getDispatchDate())
                .vehiclePlate(vehiclePlate)
                .driverName(driverName)
                .notesDespacho(d.getNotes())
                .cumplimiento(d.getCumplimiento())
                .verificadoPorUserId(v.getVerificadoPorUserId())
                .verificadoPorNombre(verificadoPorNombre)
                .fechaInicioVerificacion(v.getFechaInicioVerificacion())
                .aprobadoPorUserId(v.getAprobadoPorUserId())
                .aprobadoPorNombre(aprobadoPorNombre)
                .fechaAprobacion(v.getFechaAprobacion())
                .observacionesGenerales(v.getObservacionesGenerales())
                .detalles(detalles)
                .totalLineas(detalles.size())
                .lineasOk((int) lineasOk)
                .lineasConDiferencia((int) lineasDiferencia)
                .lineasPendientes((int) lineasPendientes)
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .version(v.getVersion())
                .build();
    }

    private DetalleVerificacionResponse toDetalleResponse(CamaraVerificacionDetalle det) {
        DispatchDetail dd = det.getDispatchDetail();
        String productName = dd.getProduct() != null ? dd.getProduct().getName() : null;
        String productCode = dd.getProduct() != null ? dd.getProduct().getCode() : null;
        Long productId     = dd.getProduct() != null ? dd.getProduct().getId() : null;

        return DetalleVerificacionResponse.builder()
                .id(det.getId())
                .dispatchDetailId(dd.getId())
                .productId(productId)
                .productName(productName)
                .productCode(productCode)
                .detalleProducto(dd.getDetalleProducto())
                .lote(dd.getLote())
                .cantidadDespachada(det.getCantidadDespachada())
                .cantidadVerificada(det.getCantidadVerificada())
                .diferencia(det.getDiferencia())
                .estadoLinea(det.getEstadoLinea())
                .tipoInconsistencia(det.getTipoInconsistencia())
                .observacionLinea(det.getObservacionLinea())
                .build();
    }

    public CamaraHistorialResponse toHistorialResponse(CamaraHistorial h) {
        return CamaraHistorialResponse.builder()
                .id(h.getId())
                .tipoEvento(h.getTipoEvento())
                .userId(h.getUserId())
                .nombreUsuario(h.getNombreUsuario())
                .estadoAnterior(h.getEstadoAnterior())
                .estadoNuevo(h.getEstadoNuevo())
                .observacion(h.getObservacion())
                .createdAt(h.getCreatedAt())
                .build();
    }

    private String resolveUserName(Long userId) {
        if (userId == null) return null;
        return userRepository.findById(userId)
                .map(u -> (u.getFirstName() + " " + u.getLastName()).trim())
                .orElse(null);
    }
}
