package com.fritomix.erp.modules.orders.application.mapper;

import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.customers.domain.entity.CustomerAddress;
import com.fritomix.erp.modules.customers.domain.repository.CustomerAddressRepository;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.repository.DispatchRepository;
import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse;
import com.fritomix.erp.modules.orders.application.dto.response.OrderResponse.OrderDetailResponse;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.orders.domain.entity.OrderDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final CustomerAddressRepository addressRepository;
    private final UserRepository userRepository;
    private final DispatchRepository dispatchRepository;

    public OrderResponse toResponse(Order order) {
        CustomerAddress addr = addressRepository.findByCustomerIdAndIsMainTrue(order.getCustomer().getId()).orElse(null);
        User user = userRepository.findById(order.getUserId()).orElse(null);
        return toResponse(order, addr, user);
    }

    public OrderResponse toResponse(Order order, CustomerAddress address) {
        return toResponse(order, address, userRepository.findById(order.getUserId()).orElse(null));
    }

    public OrderResponse toResponse(Order order, CustomerAddress address, User user) {
        List<OrderDetail> details = order.getDetails();
        if (details == null) details = Collections.emptyList();

        String phone = order.getCustomer().getPhone();
        String addrStr = order.getCustomer().getAddress();
        String cityName = null;
        String departmentName = null;
        String coordinatorName = null;
        String dispatchUserName = null;
        String dispatchDriverName = null;
        String dispatchDriverDocument = null;
        String dispatchDriverPhone = null;
        String dispatchVehiclePlate = null;
        LocalDateTime dispatchDate = null;
        Map<Long, String> detalleProductoPorProducto = new HashMap<>();
        Map<Long, java.math.BigDecimal> deliveredPorProducto = new HashMap<>();
        Map<Long, String> observationsPorProducto = new HashMap<>();
        Map<Long, String> lotePorProducto = new HashMap<>();

        if (address != null && address.getCity() != null) {
            cityName = address.getCity().getName();
            if (address.getCity().getDepartment() != null) {
                departmentName = address.getCity().getDepartment().getName();
            }
        }
        if (user != null) {
            coordinatorName = (user.getFirstName() + " " + user.getLastName()).trim();
        }

        Optional<Dispatch> dispatchOpt = dispatchRepository.findByOrderId(order.getId());
        if (dispatchOpt.isPresent()) {
            Dispatch d = dispatchOpt.get();
            if (d.getDriver() != null) {
                dispatchDriverName = d.getDriver().getName();
                dispatchDriverDocument = d.getDriver().getDocument();
                dispatchDriverPhone = d.getDriver().getPhone();
            }
            if (d.getVehicle() != null) {
                dispatchVehiclePlate = d.getVehicle().getPlate();
            }
            if (d.getUserId() != null) {
                User dispatchUser = userRepository.findById(d.getUserId()).orElse(null);
                dispatchUserName = dispatchUser != null ? (dispatchUser.getFirstName() + " " + dispatchUser.getLastName()).trim() : null;
            }
            dispatchDate = d.getDispatchDate();
            if (d.getDetails() != null) {
                d.getDetails().forEach(dd -> {
                    if (dd.getProduct() == null) return;
                    Long pid = dd.getProduct().getId();
                    if (dd.getDetalleProducto() != null && !dd.getDetalleProducto().isBlank()) {
                        detalleProductoPorProducto.put(pid, dd.getDetalleProducto());
                    }
                    if (dd.getDelivered() != null) {
                        deliveredPorProducto.put(pid, dd.getDelivered());
                    }
                    if (dd.getObservations() != null && !dd.getObservations().isBlank()) {
                        observationsPorProducto.put(pid, dd.getObservations());
                    }
                    if (dd.getLote() != null && !dd.getLote().isBlank()) {
                        lotePorProducto.put(pid, dd.getLote());
                    }
                });
            }
        }

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getBusinessName())
                .customerDocument(order.getCustomer().getDocument())
                .phone(phone)
                .address(addrStr)
                .cityName(cityName)
                .departmentName(departmentName)
                .userId(order.getUserId())
                .coordinatorName(coordinatorName)
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .total(order.getTotal())
                .pesoTotalCargue(order.getPesoTotalCargue())
                .notes(order.getNotes())
                .dispatchUserName(dispatchUserName)
                .dispatchDriverName(dispatchDriverName)
                .dispatchDriverDocument(dispatchDriverDocument)
                .dispatchDriverPhone(dispatchDriverPhone)
                .dispatchVehiclePlate(dispatchVehiclePlate)
                .dispatchDate(dispatchDate)
                .details(details.stream().map(d -> toDetailResponse(d, detalleProductoPorProducto, deliveredPorProducto, observationsPorProducto, lotePorProducto)).collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderDetailResponse toDetailResponse(OrderDetail detail, Map<Long, String> detalleProductoPorProducto,
                                                 Map<Long, java.math.BigDecimal> deliveredPorProducto,
                                                 Map<Long, String> observationsPorProducto,
                                                 Map<Long, String> lotePorProducto) {
        return OrderDetailResponse.builder()
                .id(detail.getId())
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getName())
                .productCode(detail.getProduct().getCode())
                .productType(detail.getProduct().getUnit())
                .pesoUnidad(detail.getProduct().getPesoUnidad())
                .dimension(detail.getProduct().getDimension())
                .quantity(detail.getQuantity())
                .delivered(deliveredPorProducto.get(detail.getProduct().getId()))
                .observations(observationsPorProducto.get(detail.getProduct().getId()))
                .detalleProducto(detalleProductoPorProducto.get(detail.getProduct().getId()))
                .lote(lotePorProducto.get(detail.getProduct().getId()))
                .build();
    }
}
