package com.fritomix.erp.modules.reports.application;

import com.fritomix.erp.modules.customers.domain.entity.CustomerAddress;
import com.fritomix.erp.modules.customers.domain.repository.CustomerAddressRepository;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchRepository;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.entity.OrderDetail;
import com.fritomix.erp.modules.orders.domain.repository.OrderRepository;
import com.fritomix.erp.modules.reports.api.ReportsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportsService {

    private final OrderRepository orderRepository;
    private final CustomerAddressRepository addressRepository;
    private final DispatchRepository dispatchRepository;

    @Transactional(readOnly = true)
    public List<ReportsDTO.OrderReportDTO> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatusWithDetails(status);

        Map<Long, CustomerAddress> addressByCustomer = mainAddressByCustomer(
                orders.stream()
                        .map(order -> order.getCustomer().getId())
                        .distinct()
                        .collect(Collectors.toList()));

        return orders.stream()
                .map(o -> toReport(o, addressByCustomer.get(o.getCustomer().getId())))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReportsDTO.DispatchReportDTO> getDespachados() {
        List<Dispatch> dispatches = dispatchRepository.findAllByStatusWithFetch("DESPACHADO");

        List<Long> orderIds = dispatches.stream()
                .flatMap(d -> d.getOrders().stream())
                .map(Order::getId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, List<OrderDetail>> detailsByOrder = orderIds.isEmpty() ? Map.of()
                : orderRepository.findDetailsByOrderIds(orderIds).stream()
                        .collect(Collectors.groupingBy(detail -> detail.getOrder().getId()));

        Map<Long, CustomerAddress> addressByCustomer = mainAddressByCustomer(
                dispatches.stream()
                        .flatMap(d -> d.getOrders().stream())
                        .map(o -> o.getCustomer().getId())
                        .distinct()
                        .collect(Collectors.toList()));

        return dispatches.stream()
                .map(d -> toDispatchReport(d, detailsByOrder, addressByCustomer))
                .collect(Collectors.toList());
    }

    private Map<Long, CustomerAddress> mainAddressByCustomer(List<Long> customerIds) {
        if (customerIds.isEmpty()) {
            return Map.of();
        }
        return addressRepository.findAllMainByCustomerIds(customerIds).stream()
                .collect(Collectors.toMap(
                        a -> a.getCustomer().getId(),
                        Function.identity(),
                        (first, ignored) -> first));
    }

    private ReportsDTO.OrderReportDTO toReport(Order order, CustomerAddress address) {
        String city = null;
        String department = null;
        if (address != null && address.getCity() != null) {
            city = address.getCity().getName();
            if (address.getCity().getDepartment() != null) {
                department = address.getCity().getDepartment().getName();
            }
        }

        return ReportsDTO.OrderReportDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomer().getBusinessName())
                .city(city)
                .department(department)
                .address(order.getCustomer().getAddress())
                .phone(order.getCustomer().getPhone())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .pesoTotal(computeTotalWeight(order))
                .build();
    }

    private BigDecimal computeTotalWeight(Order order) {
        return order.getDetails().stream()
                .map(this::detailWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal detailWeight(OrderDetail detail) {
        BigDecimal unitWeight = detail.getProduct().getPesoUnidad() != null
                ? detail.getProduct().getPesoUnidad()
                : BigDecimal.ZERO;
        return unitWeight.multiply(detail.getQuantity());
    }

    private ReportsDTO.DispatchReportDTO toDispatchReport(
            Dispatch dispatch,
            Map<Long, List<OrderDetail>> detailsByOrder,
            Map<Long, CustomerAddress> addressByCustomer
    ) {
        List<Order> orders = dispatch.getOrders();
        if (orders == null) orders = List.of();

        List<String> orderNumbers = orders.stream()
                .map(Order::getOrderNumber)
                .collect(Collectors.toList());

        List<String> customerNames = orders.stream()
                .filter(o -> o.getCustomer() != null)
                .map(o -> o.getCustomer().getBusinessName())
                .distinct()
                .collect(Collectors.toList());

        Order firstOrder = orders.isEmpty() ? null : orders.get(0);
        String city = null;
        String address = null;
        if (firstOrder != null && firstOrder.getCustomer() != null) {
            CustomerAddress addr = addressByCustomer.get(firstOrder.getCustomer().getId());
            if (addr != null && addr.getCity() != null) {
                city = addr.getCity().getName();
            }
            address = firstOrder.getCustomer().getAddress();
        }

        BigDecimal pesoTotal = orders.stream()
                .flatMap(o -> detailsByOrder.getOrDefault(o.getId(), List.of()).stream())
                .map(this::detailWeight)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return ReportsDTO.DispatchReportDTO.builder()
                .id(dispatch.getId())
                .dispatchNumber(dispatch.getDispatchNumber())
                .orderNumbers(orderNumbers)
                .customerNames(customerNames)
                .city(city)
                .address(address)
                .dispatchDate(dispatch.getDispatchDate())
                .driverName(dispatch.getDriver() != null ? dispatch.getDriver().getName() : null)
                .vehicleNumber(dispatch.getVehicle() != null ? dispatch.getVehicle().getVehicleNumber() : null)
                .status(dispatch.getStatus())
                .pesoTotal(pesoTotal)
                .build();
    }
}