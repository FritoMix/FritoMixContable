package com.fritomix.erp.modules.camaras.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fritomix.erp.exception.BusinessException;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.camaras.application.dto.request.CamaraAprobarRequest;
import com.fritomix.erp.modules.camaras.application.mapper.CamaraMapper;
import com.fritomix.erp.modules.camaras.domain.entity.CamaraVerificacion;
import com.fritomix.erp.modules.camaras.domain.entity.CamaraVerificacionDetalle;
import com.fritomix.erp.modules.camaras.domain.repository.CamaraHistorialRepository;
import com.fritomix.erp.modules.camaras.domain.repository.CamaraVerificacionRepository;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchDetailRepository;
import com.fritomix.erp.modules.products.domain.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CamaraServiceTest {

    @Mock
    private CamaraVerificacionRepository camaraRepository;

    @Mock
    private CamaraHistorialRepository historialRepository;

    @Mock
    private CamaraMapper camaraMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private DispatchDetailRepository dispatchDetailRepository;

    @InjectMocks
    private CamaraService camaraService;

    private JwtUserInfo userCamara;
    private JwtUserInfo userAdmin;

    @BeforeEach
    void setUp() {
        userCamara = new JwtUserInfo(10L, "camara@fritomix.com", "CAMARA", "Operador", "Camaras", List.of("PERMISSION_CAMARAS_VERIFICAR", "PERMISSION_CAMARAS_APROBAR"));
        userAdmin = new JwtUserInfo(1L, "admin@fritomix.com", "ADMIN", "Admin", "User", List.of("PERMISSION_CAMARAS_ADMIN", "PERMISSION_CAMARAS_APROBAR"));
    }

    @Test
    @DisplayName("Crear verificación para despacho sin verificación previa")
    void testCrearVerificacionParaDispatch() {
        Dispatch dispatch = Dispatch.builder()
                .id(100L)
                .dispatchNumber("DESP-00100")
                .details(new ArrayList<>())
                .build();

        DispatchDetail dd = DispatchDetail.builder()
                .id(501L)
                .product(Product.builder().id(10L).name("Papas Fritas 100g").code("P100").build())
                .quantity(new BigDecimal("50.00"))
                .build();
        dispatch.getDetails().add(dd);

        when(camaraRepository.findByDispatchId(100L)).thenReturn(Optional.empty());
        when(camaraRepository.save(any(CamaraVerificacion.class))).thenAnswer(i -> {
            CamaraVerificacion arg = i.getArgument(0);
            arg.setId(1L);
            return arg;
        });

        CamaraVerificacion result = camaraService.crearVerificacionParaDispatch(dispatch, userCamara);

        assertNotNull(result);
        assertEquals(CamaraFlow.PENDIENTE_CAMARAS, result.getEstadoCamaras());
        assertEquals(1, result.getDetalles().size());
        assertEquals(new BigDecimal("50.00"), result.getDetalles().get(0).getCantidadDespachada());
        verify(historialRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Intentar aprobar sin registrar cantidades debe fallar")
    void testAprobarSinCantidadesFalla() {
        CamaraVerificacion v = CamaraVerificacion.builder()
                .id(1L)
                .estadoCamaras(CamaraFlow.EN_VERIFICACION)
                .detalles(new ArrayList<>())
                .build();

        CamaraVerificacionDetalle det = CamaraVerificacionDetalle.builder()
                .id(10L)
                .cantidadDespachada(new BigDecimal("100"))
                .cantidadVerificada(null)
                .build();
        v.getDetalles().add(det);

        when(camaraRepository.findById(1L)).thenReturn(Optional.of(v));

        CamaraAprobarRequest request = new CamaraAprobarRequest("Confirmado OK");

        BusinessException ex = assertThrows(BusinessException.class, () ->
                camaraService.aprobarFicha(1L, request, userCamara)
        );
        assertTrue(ex.getMessage().contains("no tiene cantidad verificada registrada"));
    }

    @Test
    @DisplayName("Operador cámara no puede aprobar ficha con diferencias (requiere Admin)")
    void testAprobarConDiferenciaFallaParaCamara() {
        CamaraVerificacion v = CamaraVerificacion.builder()
                .id(1L)
                .estadoCamaras(CamaraFlow.EN_VERIFICACION)
                .detalles(new ArrayList<>())
                .build();

        CamaraVerificacionDetalle det = CamaraVerificacionDetalle.builder()
                .id(10L)
                .cantidadDespachada(new BigDecimal("100"))
                .cantidadVerificada(new BigDecimal("90"))
                .estadoLinea(CamaraFlow.LINEA_DIFERENCIA)
                .build();
        v.getDetalles().add(det);

        when(camaraRepository.findById(1L)).thenReturn(Optional.of(v));

        CamaraAprobarRequest request = new CamaraAprobarRequest("Intento aprobación");

        BusinessException ex = assertThrows(BusinessException.class, () ->
                camaraService.aprobarFicha(1L, request, userCamara)
        );
        assertTrue(ex.getMessage().contains("solo un Administrador puede aprobar con discrepancias"));
    }

    @Test
    @DisplayName("Administrador sí puede aprobar ficha con diferencias si están justificadas")
    void testAprobarConDiferenciaExitoParaAdmin() {
        CamaraVerificacion v = CamaraVerificacion.builder()
                .id(1L)
                .estadoCamaras(CamaraFlow.EN_VERIFICACION)
                .detalles(new ArrayList<>())
                .build();

        CamaraVerificacionDetalle det = CamaraVerificacionDetalle.builder()
                .id(10L)
                .cantidadDespachada(new BigDecimal("100"))
                .cantidadVerificada(new BigDecimal("90"))
                .estadoLinea(CamaraFlow.LINEA_DIFERENCIA)
                .build();
        v.getDetalles().add(det);

        when(camaraRepository.findById(1L)).thenReturn(Optional.of(v));
        when(camaraRepository.save(any(CamaraVerificacion.class))).thenAnswer(i -> i.getArgument(0));

        CamaraAprobarRequest request = new CamaraAprobarRequest("Aprobado con justificación de inventario");

        assertDoesNotThrow(() -> camaraService.aprobarFicha(1L, request, userAdmin));
        assertEquals(CamaraFlow.APROBADA, v.getEstadoCamaras());
        verify(historialRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Buscar por ID inexistente debe lanzar ResourceNotFoundException")
    void testBuscarInexistente() {
        when(camaraRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> camaraService.getFichaById(999L));
    }
}
