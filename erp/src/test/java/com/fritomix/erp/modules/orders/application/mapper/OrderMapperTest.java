package com.fritomix.erp.modules.orders.application.mapper;

import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.customers.domain.entity.City;
import com.fritomix.erp.modules.customers.domain.entity.Customer;
import com.fritomix.erp.modules.customers.domain.entity.CustomerAddress;
import com.fritomix.erp.modules.customers.domain.entity.Department;
import com.fritomix.erp.modules.customers.domain.repository.CustomerAddressRepository;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchRepository;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    @Mock
    private CustomerAddressRepository addressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DispatchRepository dispatchRepository;

    @InjectMocks
    private OrderMapper mapper;

    private Customer customer(long id) {
        return Customer.builder()
                .id(id)
                .businessName("Distribuidora " + id)
                .document("900" + id)
                .phone("300000" + id)
                .address("Calle " + id)
                .build();
    }

    private Order order(long id) {
        return Order.builder()
                .id(id)
                .orderNumber("ORD-" + id)
                .customer(customer(id))
                .status("pendiente")
                .total(new BigDecimal("12"))
                .build();
    }

    private Product product(long id) {
        return Product.builder()
                .id(id)
                .name("Producto " + id)
                .code("P" + id)
                .unit("caja")
                .pesoUnidad(new BigDecimal("2"))
                .dimension(new BigDecimal("1.5"))
                .presentation(10)
                .weightGrams(500)
                .build();
    }

    private OrderDetail detail(long id, Product product, String qty) {
        return OrderDetail.builder().id(id).product(product).quantity(new BigDecimal(qty)).build();
    }

    private void assertDecimal(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }

    @Test
    void mapeaCamposBasicosConTipoPedidoPorDefecto() {
        Order order = order(1L);

        OrderResponse response = mapper.toResponse(order, null, null, List.of(), null);

        assertEquals(1L, response.id());
        assertEquals("ORD-1", response.orderNumber());
        assertEquals("Distribuidora 1", response.customerName());
        assertEquals("9001", response.customerDocument());
        assertEquals("3000001", response.phone());
        assertEquals("Calle 1", response.address());
        assertEquals("pedido_unico", response.tipoPedido());
        assertNull(response.cityName());
        assertNull(response.coordinatorName());
    }

    @Test
    void mapeaCiudadYDepartamento() {
        Department dept = Department.builder().id(5L).name("Antioquia").build();
        City city = City.builder().id(7L).name("Medellín").department(dept).build();
        CustomerAddress addr = CustomerAddress.builder().city(city).build();

        OrderResponse response = mapper.toResponse(order(2L), addr, null, List.of(), null);

        assertEquals("Medellín", response.cityName());
        assertEquals("Antioquia", response.departmentName());
    }

    @Test
    void mapeaNombreCoordinadorYPreservaTipoPedido() {
        Order order = order(3L);
        order.setTipoPedido("multipedido");
        User user = User.builder().id(9L).firstName("Luisa").lastName("Gómez").build();

        OrderResponse response = mapper.toResponse(order, null, user, List.of(), null);

        assertEquals("Luisa Gómez", response.coordinatorName());
        assertEquals("multipedido", response.tipoPedido());
    }

    @Test
    void mapeaAprobadorDesdeRepositorio() {
        Order order = order(4L);
        order.setApprovedById(11L);
        order.setApprovedAt(LocalDateTime.of(2026, 1, 1, 10, 0));
        when(userRepository.findById(11L)).thenReturn(Optional.of(
                User.builder().id(11L).firstName("Mario").lastName("Ruiz").build()));

        OrderResponse response = mapper.toResponse(order, null, null, List.of(), null);

        assertEquals("Mario Ruiz", response.approvedByName());
        assertEquals(11L, response.approvedById());
        assertEquals(LocalDateTime.of(2026, 1, 1, 10, 0), response.approvedAt());
    }

    @Test
    void mapeaDatosDeDespachoDesdeDriverYPlaca() {
        Order order = order(5L);
        Driver driver = Driver.builder().id(1L).name("Pedro").document("CC1").phone("311").build();
        User dispatchUser = User.builder().firstName("Eva").lastName("Diaz").build();
        when(userRepository.findById(50L)).thenReturn(Optional.of(dispatchUser));

        Dispatch dispatch = Dispatch.builder()
                .driver(driver)
                .vehiclePlate("  ABC123  ")
                .userId(50L)
                .dispatchDate(LocalDateTime.of(2026, 2, 2, 8, 0))
                .build();

        OrderResponse response = mapper.toResponse(order, null, null, List.of(), dispatch);

        assertEquals("Pedro", response.dispatchDriverName());
        assertEquals("CC1", response.dispatchDriverDocument());
        assertEquals("311", response.dispatchDriverPhone());
        assertEquals("ABC123", response.dispatchVehicleNumber());
        assertEquals("Eva Diaz", response.dispatchUserName());
        assertEquals(LocalDateTime.of(2026, 2, 2, 8, 0), response.dispatchDate());
    }

    @Test
    void usaVehiculoCuandoNoHayPlaca() {
        Order order = order(6L);
        Vehicle vehicle = Vehicle.builder().id(1L).vehicleNumber("XYZ789").build();
        Dispatch dispatch = Dispatch.builder().vehicle(vehicle).build();

        OrderResponse response = mapper.toResponse(order, null, null, List.of(), dispatch);

        assertEquals("XYZ789", response.dispatchVehicleNumber());
    }

    @Test
    void recalculaPesoCuandoEsCero() {
        Order order = order(7L);
        order.setPesoTotalCargue(BigDecimal.ZERO);
        Product product = Product.builder().id(1L).name("P").pesoUnidad(new BigDecimal("2.5")).build();

        OrderResponse response = mapper.toResponse(order, null, null, List.of(detail(1L, product, "4")), null);

        assertDecimal("10.0", response.pesoTotalCargue());
    }

    @Test
    void conservaPesoCuandoEsPositivo() {
        Order order = order(8L);
        order.setPesoTotalCargue(new BigDecimal("42"));

        OrderResponse response = mapper.toResponse(order, null, null, List.of(), null);

        assertDecimal("42", response.pesoTotalCargue());
    }

    @Test
    void mapeaDetallesConDatosDeDespacho() {
        Order order = order(9L);
        Product product = product(1L);
        order.getDetails().add(detail(100L, product, "3"));

        DispatchDetail dd = DispatchDetail.builder()
                .product(product)
                .delivered(new BigDecimal("2"))
                .observations("dañado")
                .detalleProducto("caja negra")
                .lote("L-01")
                .build();
        Dispatch dispatch = Dispatch.builder().details(List.of(dd)).build();
        when(dispatchRepository.findAllByOrderId(9L)).thenReturn(List.of(dispatch));

        OrderResponse response = mapper.toResponse(order, null, null);

        OrderResponse.OrderDetailResponse dr = response.details().get(0);
        assertEquals(100L, dr.id());
        assertEquals(1L, dr.productId());
        assertDecimal("2", dr.delivered());
        assertEquals("dañado", dr.observations());
        assertEquals("caja negra", dr.detalleProducto());
        assertEquals("L-01", dr.lote());
    }

    @Test
    void toResponseConSoloOrderUsaRepositorios() {
        Order order = order(10L);
        when(addressRepository.findByCustomerIdAndIsMainTrue(10L))
                .thenReturn(Optional.of(CustomerAddress.builder()
                        .city(City.builder().name("Bogotá").build())
                        .build()));

        OrderResponse response = mapper.toResponse(order);

        assertEquals("Bogotá", response.cityName());
        assertEquals("ORD-10", response.orderNumber());
    }
}