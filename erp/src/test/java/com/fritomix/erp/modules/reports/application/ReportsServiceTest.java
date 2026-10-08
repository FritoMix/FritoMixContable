package com.fritomix.erp.modules.reports.application;

import com.fritomix.erp.modules.customers.domain.entity.City;
import com.fritomix.erp.modules.customers.domain.entity.Customer;
import com.fritomix.erp.modules.customers.domain.entity.CustomerAddress;
import com.fritomix.erp.modules.customers.domain.entity.Department;
import com.fritomix.erp.modules.customers.domain.repository.CustomerAddressRepository;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchRepository;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.entity.OrderDetail;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import com.fritomix.erp.modules.products.domain.entity.Product;
import com.fritomix.erp.modules.reports.application.dto.ReportsDTO;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportsServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerAddressRepository addressRepository;
    @Mock
    private DispatchRepository dispatchRepository;

    @InjectMocks
    private ReportsService service;

    private void assertDecimal(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "esperado " + expected + " pero fue " + actual);
    }

    private Customer customer(long id) {
        return Customer.builder()
                .id(id)
                .businessName("Cliente " + id)
                .address("Dir " + id)
                .phone("300" + id)
                .build();
    }

    private OrderDetail detail(long productId, String pesoUnidad, String qty) {
        Product product = Product.builder().id(productId).pesoUnidad(new BigDecimal(pesoUnidad)).build();
        return OrderDetail.builder().product(product).quantity(new BigDecimal(qty)).build();
    }

    private Order order(long id, Customer customer, OrderDetail detail) {
        return Order.builder()
                .id(id)
                .orderNumber("ORD-" + id)
                .customer(customer)
                .orderDate(LocalDateTime.of(2026, 3, 1, 9, 0))
                .status("despachado")
                .details(List.of(detail))
                .build();
    }

    private CustomerAddress mainAddress(Customer customer, String cityName, String deptName) {
        Department dept = deptName == null ? null : Department.builder().name(deptName).build();
        City city = cityName == null ? null : City.builder().name(cityName).department(dept).build();
        return CustomerAddress.builder().customer(customer).city(city).isMain(true).build();
    }

    @Test
    void getOrdersByStatusMapeaCiudadYCalculaPeso() {
        Customer c1 = customer(1L);
        Customer c2 = customer(2L);
        Order o1 = order(10L, c1, detail(100L, "2.5", "4"));
        Order o2 = order(11L, c2, detail(101L, "1", "3"));
        when(orderRepository.findByStatusWithDetails("pendiente")).thenReturn(List.of(o1, o2));
        when(addressRepository.findAllMainByCustomerIds(List.of(1L, 2L)))
                .thenReturn(List.of(mainAddress(c1, "Medellín", "Antioquia")));

        List<ReportsDTO.OrderReportDTO> report = service.getOrdersByStatus("pendiente");

        assertEquals(2, report.size());
        ReportsDTO.OrderReportDTO first = report.get(0);
        assertEquals(10L, first.id());
        assertEquals("ORD-10", first.orderNumber());
        assertEquals("Cliente 1", first.customerName());
        assertEquals("Medellín", first.city());
        assertEquals("Antioquia", first.department());
        assertEquals("Dir 1", first.address());
        assertEquals("3001", first.phone());
        assertDecimal("10", first.pesoTotal());

        ReportsDTO.OrderReportDTO second = report.get(1);
        assertNull(second.city());
        assertNull(second.department());
        assertDecimal("3", second.pesoTotal());
    }

    @Test
    void getOrdersByStatusSinPedidosNoConsultaDirecciones() {
        when(orderRepository.findByStatusWithDetails("cancelado")).thenReturn(List.of());

        assertTrue(service.getOrdersByStatus("cancelado").isEmpty());
    }

    @Test
    void getDespachadosMapeaPedidosClientesYChofer() {
        Customer c1 = customer(1L);
        Order o1 = order(10L, c1, detail(100L, "2", "3"));
        Order o2 = order(11L, c1, detail(101L, "1", "1"));
        Driver driver = Driver.builder().id(5L).name("Pedro").build();
        Dispatch dispatch = Dispatch.builder()
                .id(50L)
                .dispatchNumber("DESP-50")
                .orders(List.of(o1, o2))
                .driver(driver)
                .vehiclePlate("  ABC123 ")
                .status("DESPACHADO")
                .build();
        when(dispatchRepository.findAllByStatusWithFetch("DESPACHADO")).thenReturn(List.of(dispatch));
        OrderDetail d1 = detail(100L, "2", "3");
        d1.setOrder(o1);
        OrderDetail d2 = detail(101L, "1", "1");
        d2.setOrder(o2);
        when(orderRepository.findDetailsByOrderIds(List.of(10L, 11L)))
                .thenReturn(List.of(d1, d2));
        when(addressRepository.findAllMainByCustomerIds(List.of(1L)))
                .thenReturn(List.of(mainAddress(c1, "Bogotá", null)));

        List<ReportsDTO.DispatchReportDTO> report = service.getDespachados();

        assertEquals(1, report.size());
        ReportsDTO.DispatchReportDTO d = report.get(0);
        assertEquals(50L, d.id());
        assertEquals("DESP-50", d.dispatchNumber());
        assertEquals(List.of("ORD-10", "ORD-11"), d.orderNumbers());
        assertEquals(List.of("Cliente 1"), d.customerNames());
        assertEquals("Bogotá", d.city());
        assertEquals("Dir 1", d.address());
        assertEquals("Pedro", d.driverName());
        assertEquals("ABC123", d.vehicleNumber());
        assertEquals("DESPACHADO", d.status());
        assertDecimal("7.00", d.pesoTotal());
    }

    @Test
    void getDespachadosSinPedidosUsaListasVacias() {
        Vehicle vehicle = Vehicle.builder().id(9L).vehicleNumber("XYZ").build();
        Dispatch dispatch = Dispatch.builder().id(51L).orders(List.of()).vehicle(vehicle).build();
        when(dispatchRepository.findAllByStatusWithFetch("DESPACHADO")).thenReturn(List.of(dispatch));

        List<ReportsDTO.DispatchReportDTO> report = service.getDespachados();

        ReportsDTO.DispatchReportDTO d = report.get(0);
        assertTrue(d.orderNumbers().isEmpty());
        assertTrue(d.customerNames().isEmpty());
        assertNull(d.city());
        assertNull(d.address());
        assertEquals("XYZ", d.vehicleNumber());
        assertDecimal("0.00", d.pesoTotal());
    }
}