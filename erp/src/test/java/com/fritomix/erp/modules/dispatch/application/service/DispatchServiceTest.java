package com.fritomix.erp.modules.dispatch.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.exception.PedidoYaDespachadoException;
import com.fritomix.erp.exception.ProductoNotFoundException;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.application.dto.JwtUserInfo;
import com.fritomix.erp.modules.auth.domain.entity.Role;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.dispatch.application.dto.request.ConfirmarPlacaRequest;
import com.fritomix.erp.modules.dispatch.application.dto.request.DispatchRequest;
import com.fritomix.erp.modules.dispatch.application.dto.response.DespachadorDto;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse;
import com.fritomix.erp.modules.dispatch.application.mapper.DispatchMapper;
import com.fritomix.erp.modules.dispatch.domain.entity.Arrume;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchRepository;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.drivers.domain.repository.DriverRepository;
import com.fritomix.erp.modules.orders.application.service.OrderStatusRules;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.products.domain.repository.ProductRepository;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import com.fritomix.erp.modules.vehicles.domain.repository.VehicleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock private DispatchRepository dispatchRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private DriverRepository driverRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private ProductRepository productRepository;
    @Mock private DispatchMapper mapper;
    @Mock private DispatchNotifier dispatchNotifier;
    @Mock private UserRepository userRepository;
    @Mock private DispatchFacturaStore facturaStore;

    @InjectMocks private DispatchService service;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private Order order(Long id, String status, String number) {
        return Order.builder().id(id).orderNumber(number).status(status).build();
    }

    private Dispatch dispatch(Long id, String status) {
        return Dispatch.builder().id(id).dispatchNumber("DES-1").status(status).build();
    }

    @Test
    void findAllSinResultadosDevuelvePaginaVacia() {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Long> ids = new PageImpl<>(List.of(), pageable, 0);
        when(dispatchRepository.findIds(null, pageable)).thenReturn(ids);

        PageResponse<DispatchResponse> result = service.findAll(null, null, pageable);

        assertEquals(0, result.content().size());
        assertEquals(0, result.totalElements());
    }

    @Test
    void findAllConStatusesMapeaContenido() {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Long> ids = new PageImpl<>(List.of(1L), pageable, 1);
        when(dispatchRepository.findIdsByStatuses(null, List.of("PENDIENTE"), pageable)).thenReturn(ids);
        Dispatch d = dispatch(1L, "PENDIENTE");
        when(dispatchRepository.findAllWithFetchByIds(List.of(1L))).thenReturn(List.of(d));
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(mock(DispatchResponse.class));

        PageResponse<DispatchResponse> result = service.findAll(null, List.of("PENDIENTE"), pageable);

        assertEquals(1, result.content().size());
    }

    @Test
    void findByIdEncontradoYNulo() {
        Dispatch d = dispatch(1L, "PENDIENTE");
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(d));
        when(mapper.toResponse(d)).thenReturn(mock(DispatchResponse.class));

        assertNotNull(service.findById(1L));

        when(dispatchRepository.findById(9L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }

    @Test
    void findAssignedToDriverVacioYConContenido() {
        Pageable pageable = PageRequest.of(0, 10);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new JwtUserInfo(5L, "d@x.com", "DRIVER", "D", "R", List.of()), null, List.of()));
        PageImpl<Long> empty = new PageImpl<>(List.of(), pageable, 0);
        when(dispatchRepository.findAssignedIds(null, 5L, pageable)).thenReturn(empty);

        assertEquals(0, service.findAssignedToDriver(null, pageable).content().size());

        PageImpl<Long> ids = new PageImpl<>(List.of(1L), pageable, 1);
        when(dispatchRepository.findAssignedIds(null, 5L, pageable)).thenReturn(ids);
        Dispatch d = dispatch(1L, "PENDIENTE");
        when(dispatchRepository.findAllWithFetchByIds(List.of(1L))).thenReturn(List.of(d));
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(mock(DispatchResponse.class));

        assertEquals(1, service.findAssignedToDriver(null, pageable).content().size());
    }

    @Test
    void findAssignedByMeSinUsuarioDevuelveVacio() {
        Pageable pageable = PageRequest.of(0, 10);
        assertEquals(0, service.findAssignedByMe(pageable).totalElements());
    }

    @Test
    void misConfirmacionesSinUsuarioDevuelveVacio() {
        Pageable pageable = PageRequest.of(0, 10);
        assertEquals(0, service.misConfirmaciones(pageable).totalElements());
    }

    @Test
    void despachadoresFiltraActivos() {
        User u1 = User.builder().id(1L).firstName("Ana").lastName("Soto").email("a@x.com").enabled(true)
                .role(Role.builder().name("DESPACHADOR3").build()).build();
        User u2 = User.builder().id(2L).enabled(false).role(Role.builder().name("DESPACHADOR3").build()).build();
        when(userRepository.findByRoleName("DESPACHADOR3")).thenReturn(List.of(u1, u2));

        List<DespachadorDto> list = service.despachadores();

        assertEquals(1, list.size());
        assertEquals(1L, list.get(0).id());
    }

    @Test
    void confirmarPlacaValidaTransicionYSolicitaObservacion() {
        Dispatch d = dispatch(1L, DispatchFlow.STATUS_VEHICULO_ASIGNADO);
        d.setVehiclePlate("OLD123");
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(d));
        User desp = User.builder().id(10L).enabled(true).role(Role.builder().name("DESPACHADOR3").build()).build();
        when(userRepository.findById(10L)).thenReturn(Optional.of(desp));
        when(vehicleRepository.findByVehicleNumber("ABC123")).thenReturn(Optional.empty());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new JwtUserInfo(5L, "c@x.com", "ADMIN", "C", "L", List.of()), null, List.of()));
        when(dispatchRepository.save(any(Dispatch.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(mock(DispatchResponse.class));

        assertThrows(IllegalArgumentException.class,
                () -> service.confirmarPlaca(1L, new ConfirmarPlacaRequest(10L, "ABC123", null)));

        service.confirmarPlaca(1L, new ConfirmarPlacaRequest(10L, "ABC123", "Cambio de placa"));
        verify(dispatchNotifier).notifyPlacaConfirmada(any(Dispatch.class), eq(desp));
    }

    @Test
    void confirmarPlacaRechazaSiNoEsVehiculoAsignado() {
        Dispatch d = dispatch(1L, DispatchFlow.STATUS_PENDIENTE);
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(d));

        assertThrows(IllegalArgumentException.class,
                () -> service.confirmarPlaca(1L, new ConfirmarPlacaRequest(10L, "ABC123", null)));
    }

    @Test
    void confirmarPlacaRechazaDespachadorInvalido() {
        Dispatch d = dispatch(1L, DispatchFlow.STATUS_VEHICULO_ASIGNADO);
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(d));
        User desp = User.builder().id(10L).enabled(true).role(Role.builder().name("CAJERO").build()).build();
        when(userRepository.findById(10L)).thenReturn(Optional.of(desp));

        assertThrows(IllegalArgumentException.class,
                () -> service.confirmarPlaca(1L, new ConfirmarPlacaRequest(10L, "ABC123", null)));
    }

    @Test
    void asignarPlacaExitoso() {
        Order o = order(1L, OrderStatusRules.STATUS_LISTO_PRODUCCION, "PED-1");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(o));
        when(dispatchRepository.findAllByOrderId(1L)).thenReturn(List.of());
        Vehicle v = Vehicle.builder().id(5L).vehicleNumber("ABC123").build();
        when(vehicleRepository.findByVehicleNumber("ABC123")).thenReturn(Optional.of(v));
        when(dispatchRepository.save(any(Dispatch.class))).thenAnswer(inv -> {
            Dispatch dd = inv.getArgument(0);
            dd.setId(2L);
            return dd;
        });
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        new JwtUserInfo(5L, "u@x.com", "ADMIN", "U", "N", List.of()), null, List.of()));
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(mock(DispatchResponse.class));

        DispatchResponse resp = service.asignarPlaca(1L, "abc123");

        assertNotNull(resp);
        verify(dispatchNotifier).notifyCreated(any(Dispatch.class), any(), eq(null), eq(v), any());
    }

    @Test
    void asignarPlacaRechazaEstadoInvalido() {
        Order o = order(1L, OrderStatusRules.STATUS_APROBADO, "PED-1");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(o));

        assertThrows(IllegalArgumentException.class, () -> service.asignarPlaca(1L, "ABC123"));
    }

    @Test
    void createConNumeroDuplicadoLanzaExcepcion() {
        DispatchRequest req = DispatchRequest.builder().dispatchNumber("DES-1").build();
        when(dispatchRepository.existsByDispatchNumber("DES-1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void createValidaTipoPedido() {
        DispatchRequest req = DispatchRequest.builder().dispatchNumber("DES-1").tipoPedido("malo").build();
        when(dispatchRepository.existsByDispatchNumber("DES-1")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void createRequiereAlMenosUnPedido() {
        DispatchRequest req = DispatchRequest.builder().dispatchNumber("DES-1").build();
        when(dispatchRepository.existsByDispatchNumber("DES-1")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void createPedidoUnicoSoloUnPedido() {
        DispatchRequest req = DispatchRequest.builder().dispatchNumber("DES-1")
                .tipoPedido("pedido_unico").orderIds(List.of(1L, 2L)).build();
        when(dispatchRepository.existsByDispatchNumber("DES-1")).thenReturn(false);
        when(orderRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(order(1L, "LISTO_PRODUCCION", "PED-1"), order(2L, "LISTO_PRODUCCION", "PED-2")));

        assertThrows(IllegalArgumentException.class, () -> service.create(req));
    }

    @Test
    void createConPedidoYaDespachadoLanzaExcepcion() {
        DispatchRequest req = DispatchRequest.builder().dispatchNumber("DES-1")
                .tipoPedido("pedido_unico").orderId(1L).build();
        when(dispatchRepository.existsByDispatchNumber("DES-1")).thenReturn(false);
        Order o = order(1L, OrderStatusRules.STATUS_LISTO_PRODUCCION, "PED-1");
        when(orderRepository.findAllById(List.of(1L))).thenReturn(List.of(o));
        when(dispatchRepository.findAllByOrderId(1L)).thenReturn(List.of(dispatch(5L, "DESPACHADO")));

        assertThrows(PedidoYaDespachadoException.class, () -> service.create(req));
    }

    @Test
    void createCompletoConDetallesYArrumes() {
        DispatchRequest req = DispatchRequest.builder().dispatchNumber("DES-1")
                .tipoPedido("pedido_unico").orderId(1L)
                .driverId(1L).vehicleId(1L).userId(5L).status("PENDIENTE")
                .details(List.of(DispatchRequest.DispatchDetailRequest.builder()
                        .productId(1L).quantity(BigDecimal.ONE).build()))
                .arrumes(List.of(DispatchRequest.ArrumeRequest.builder().numArrume(1).arrumeProducto("A").cantidad(BigDecimal.ONE).build()))
                .build();
        when(dispatchRepository.existsByDispatchNumber("DES-1")).thenReturn(false);
        Order o = order(1L, OrderStatusRules.STATUS_LISTO_PRODUCCION, "PED-1");
        when(orderRepository.findAllById(List.of(1L))).thenReturn(List.of(o));
        when(dispatchRepository.findAllByOrderId(1L)).thenReturn(List.of());
        when(driverRepository.findById(1L)).thenReturn(Optional.of(Driver.builder().id(1L).build()));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(Vehicle.builder().id(1L).build()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(Product.builder().id(1L).build()));
        when(dispatchRepository.save(any(Dispatch.class))).thenAnswer(inv -> {
            Dispatch dd = inv.getArgument(0);
            dd.setId(10L);
            return dd;
        });
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(mock(DispatchResponse.class));

        assertNotNull(service.create(req));
    }

    @Test
    void updateNoPermiteEditarCerrado() {
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(dispatch(1L, DispatchFlow.STATUS_DESPACHADO)));

        assertThrows(IllegalArgumentException.class,
                () -> service.update(1L, DispatchRequest.builder().build()));
    }

    @Test
    void updateCambiaTipoPedidoYPedidos() {
        Dispatch d = dispatch(1L, DispatchFlow.STATUS_PENDIENTE);
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(d));
        DispatchRequest req = DispatchRequest.builder().tipoPedido("pedido_multipedido").orderIds(List.of(1L,2L)).build();
        Order o1 = order(1L, OrderStatusRules.STATUS_LISTO_PRODUCCION, "PED-1");
        Order o2 = order(2L, OrderStatusRules.STATUS_LISTO_PRODUCCION, "PED-2");
        when(orderRepository.findAllById(List.of(1L,2L))).thenReturn(List.of(o1,o2));
        when(dispatchRepository.findAllByOrderId(1L)).thenReturn(List.of());
        when(dispatchRepository.findAllByOrderId(2L)).thenReturn(List.of());
        when(dispatchRepository.save(any(Dispatch.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(mock(DispatchResponse.class));

        service.update(1L, req);
        assertEquals("pedido_multipedido", d.getTipoPedido());
    }

    @Test
    void updateRechazaPedidosVacios() {
        Dispatch d = dispatch(1L, DispatchFlow.STATUS_PENDIENTE);
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(d));
        DispatchRequest req = DispatchRequest.builder().orderIds(List.of()).build();

        assertThrows(IllegalArgumentException.class, () -> service.update(1L, req));
    }

    @Test
    void updateCambiaConductorVehiculoNumeroFactura() {
        Dispatch d = dispatch(1L, DispatchFlow.STATUS_PENDIENTE);
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(d));
        DispatchRequest req = DispatchRequest.builder()
                .driverId(1L).vehicleId(1L).dispatchNumber("DES-X").numeroFactura("F-1").status("VEHICULO_ASIGNADO").build();
        when(driverRepository.findById(1L)).thenReturn(Optional.of(Driver.builder().id(1L).build()));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(Vehicle.builder().id(1L).build()));
        when(dispatchRepository.save(any(Dispatch.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(mock(DispatchResponse.class));

        service.update(1L, req);
        assertEquals("DES-X", d.getDispatchNumber());
    }

    @Test
    void deleteNoPermiteCerrado() {
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(dispatch(1L, DispatchFlow.STATUS_DESPACHADO)));

        assertThrows(IllegalArgumentException.class, () -> service.delete(1L));
    }

    @Test
    void deleteExitoso() {
        Dispatch d = dispatch(1L, DispatchFlow.STATUS_PENDIENTE);
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(d));

        service.delete(1L);
        verify(dispatchRepository).delete(d);
    }

    @Test
    void updateStatusDespapchadoNotifica() {
        Dispatch d = dispatch(1L, DispatchFlow.STATUS_CONDUCTOR_ASIGNADO);
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(d));
        when(dispatchRepository.save(any(Dispatch.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(mock(DispatchResponse.class));

        service.updateStatus(1L, "despachado");

        verify(dispatchNotifier).notifyDispatched(d);
    }

    @Test
    void updateStatusInvalidoLanzaExcepcion() {
        Dispatch d = dispatch(1L, DispatchFlow.STATUS_PENDIENTE);
        when(dispatchRepository.findById(1L)).thenReturn(Optional.of(d));

        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(1L, "MALO"));
    }

    @Test
    void findHistoryByOrderIdRetornaLista() {
        when(dispatchRepository.findAllByOrderId(1L)).thenReturn(List.of(dispatch(1L, "DESPACHADO")));
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(mock(DispatchResponse.class));

        assertEquals(1, service.findHistoryByOrderId(1L).size());
    }

    @Test
    void findByDateRangeVacioYConContenido() {
        Pageable pageable = PageRequest.of(0, 10);
        LocalDateTime desde = LocalDateTime.now().minusDays(1);
        LocalDateTime hasta = LocalDateTime.now();
        PageImpl<Long> empty = new PageImpl<>(List.of(), pageable, 0);
        when(dispatchRepository.findIdsBetweenDates(desde, hasta, pageable)).thenReturn(empty);

        assertEquals(0, service.findByDateRange(desde, hasta, pageable).content().size());

        PageImpl<Long> ids = new PageImpl<>(List.of(1L), pageable, 1);
        when(dispatchRepository.findIdsBetweenDates(desde, hasta, pageable)).thenReturn(ids);
        when(dispatchRepository.findAllWithFetchByIds(List.of(1L))).thenReturn(List.of(dispatch(1L, "PENDIENTE")));
        when(mapper.toResponse(any(Dispatch.class))).thenReturn(mock(DispatchResponse.class));

        assertEquals(1, service.findByDateRange(desde, hasta, pageable).content().size());
    }
}