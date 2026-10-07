package com.fritomix.erp.modules.dispatch.application.mapper;

import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.customers.domain.entity.Customer;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse;
import com.fritomix.erp.modules.dispatch.domain.entity.Arrume;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.entity.OrderDetail;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchMapperTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DispatchMapper mapper;

    private void assertDecimal(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "esperado " + expected + " pero fue " + actual);
    }

    private Product product(long id, String pesoUnidad, String dimension) {
        return Product.builder()
                .id(id).name("Prod " + id).code("C" + id)
                .pesoUnidad(new BigDecimal(pesoUnidad))
                .dimension(new BigDecimal(dimension))
                .build();
    }

    private Order order(long id, Product product, String qty) {
        OrderDetail detail = OrderDetail.builder().product(product).quantity(new BigDecimal(qty)).build();
        return Order.builder()
                .id(id)
                .orderNumber("ORD-" + id)
                .customer(Customer.builder().id(id).businessName("Cliente " + id).build())
                .pesoTotalCargue(new BigDecimal("5"))
                .details(List.of(detail))
                .build();
    }

    @Test
    void mapeaSinOrdersNiDetallesConValoresPorDefecto() {
        Dispatch dispatch = Dispatch.builder().id(1L).details(null).orders(null).build();

        DispatchResponse response = mapper.toResponse(dispatch);

        assertEquals(1L, response.id());
        assertTrue(response.orders().isEmpty());
        assertTrue(response.details().isEmpty());
        assertTrue(response.arrumes().isEmpty());
        assertDecimal("0", response.pesoTotal());
        assertDecimal("0", response.totalDimension());
    }

    @Test
    void calculaPesoYDimensionConEscalaDos() {
        Order order = order(1L, product(1L, "2.5", "1.5"), "3");
        Dispatch dispatch = Dispatch.builder().orders(List.of(order)).build();

        DispatchResponse response = mapper.toResponse(dispatch);

        assertDecimal("7.50", response.pesoTotal());
        assertDecimal("4.50", response.totalDimension());
        assertDecimal("7.50", response.pesoTotalCargue());
    }

    @Test
    void mapeaOrderInfoConFacturaYPrimerPedido() {
        Order order = order(1L, product(1L, "2", "1"), "3");
        Dispatch dispatch = Dispatch.builder()
                .orders(List.of(order))
                .facturasPorPedido(Map.of(1L, "FAC-1"))
                .build();

        DispatchResponse response = mapper.toResponse(dispatch);

        assertEquals(1, response.orders().size());
        assertEquals("FAC-1", response.orders().get(0).numeroFactura());
        assertEquals("Cliente 1", response.orders().get(0).clientName());
        assertEquals(1L, response.orderId());
        assertEquals(order.getOrderNumber(), response.orderNumber());
    }

    @Test
    void mapeaDriverYVehicleUsandoNumeroDeVehiculo() {
        Driver driver = Driver.builder().id(3L).name("Pedro").document("CC3").build();
        Vehicle vehicle = Vehicle.builder().id(4L).vehicleNumber("XYZ").type("Camión").build();
        Dispatch dispatch = Dispatch.builder().driver(driver).vehicle(vehicle).build();

        DispatchResponse response = mapper.toResponse(dispatch);

        assertEquals(3L, response.driverId());
        assertEquals("Pedro", response.driverName());
        assertEquals("CC3", response.driverDocument());
        assertEquals(4L, response.vehicleId());
        assertEquals("Camión", response.vehicleType());
        assertEquals("XYZ", response.vehicleNumber());
        assertEquals("XYZ", response.vehiclePlate());
    }

    @Test
    void placaTienePrioridadYSeRecorta() {
        Vehicle vehicle = Vehicle.builder().id(4L).vehicleNumber("XYZ").build();
        Dispatch dispatch = Dispatch.builder().vehicle(vehicle).vehiclePlate("  ABC  ").build();

        DispatchResponse response = mapper.toResponse(dispatch);

        assertEquals("ABC", response.vehiclePlate());
        assertEquals("ABC", response.vehicleNumber());
    }

    @Test
    void mapeaNombresDeUsuariosDesdeRepositorio() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(user("Ana", "Soto")));
        when(userRepository.findById(20L)).thenReturn(Optional.of(user("Luis", "Mora")));
        when(userRepository.findById(30L)).thenReturn(Optional.of(user("Eva", "Diaz")));

        Dispatch dispatch = Dispatch.builder()
                .userId(10L)
                .despachadorUserId(20L)
                .confirmadoPorUserId(30L)
                .build();

        DispatchResponse response = mapper.toResponse(dispatch);

        assertEquals("Ana Soto", response.dispatchUserName());
        assertEquals(20L, response.despachadorUserId());
        assertEquals("Luis Mora", response.despachadorNombre());
        assertEquals(30L, response.confirmadoPorUserId());
        assertEquals("Eva Diaz", response.confirmadoPorNombre());
    }

    @Test
    void usuarioInexistenteQuedaComoNull() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Dispatch dispatch = Dispatch.builder().userId(99L).build();

        DispatchResponse response = mapper.toResponse(dispatch);

        assertNull(response.dispatchUserName());
    }

    @Test
    void mapeaDetallesConYsinProducto() {
        Product product = product(1L, "2", "1");
        DispatchDetail conProducto = DispatchDetail.builder()
                .id(100L).product(product).quantity(new BigDecimal("5"))
                .delivered(new BigDecimal("4")).observations("ok")
                .detalleProducto("caja").lote("L1").build();
        DispatchDetail sinProducto = DispatchDetail.builder().id(101L).quantity(BigDecimal.ONE).build();

        Dispatch dispatch = Dispatch.builder().details(List.of(conProducto, sinProducto)).build();
        DispatchResponse response = mapper.toResponse(dispatch);

        DispatchResponse.DispatchDetailResponse first = response.details().get(0);
        assertEquals(100L, first.id());
        assertEquals(1L, first.productId());
        assertEquals("Prod 1", first.productName());
        assertEquals("C1", first.productCode());
        assertEquals("ok", first.observations());
        assertEquals("caja", first.detalleProducto());
        assertEquals("L1", first.lote());

        DispatchResponse.DispatchDetailResponse second = response.details().get(1);
        assertEquals(101L, second.id());
        assertNull(second.productId());
    }

    @Test
    void mapeaArrumes() {
        Arrume arrume = Arrume.builder()
                .id(7L).numArrume(1).arrumeProducto("Caja")
                .cantidad(new BigDecimal("12")).lote("L-9").build();
        Dispatch dispatch = Dispatch.builder().arrumes(List.of(arrume)).build();

        DispatchResponse response = mapper.toResponse(dispatch);

        assertEquals(1, response.arrumes().size());
        assertEquals(7L, response.arrumes().get(0).id());
        assertEquals(1, response.arrumes().get(0).numArrume());
        assertEquals("Caja", response.arrumes().get(0).arrumeProducto());
        assertDecimal("12", response.arrumes().get(0).cantidad());
        assertEquals("L-9", response.arrumes().get(0).lote());
    }

    @Test
    void sinPedidosNiDetallesDevuelveListasVacias() {
        Dispatch dispatch = Dispatch.builder()
                .orders(null)
                .details(null)
                .arrumes(null)
                .build();

        DispatchResponse response = mapper.toResponse(dispatch);

        assertTrue(response.orders().isEmpty());
        assertTrue(response.details().isEmpty());
        assertTrue(response.arrumes().isEmpty());
        assertNull(response.orderId());
    }

    private User user(String firstName, String lastName) {
        return User.builder().firstName(firstName).lastName(lastName).build();
    }
}