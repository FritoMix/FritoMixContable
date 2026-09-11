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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<Dispatch> dispatches = dispatchRepository.findAllByOrderId(order.getId());
        Dispatch dispatch = dispatches.isEmpty() ? null : dispatches.get(0);
        return toResponse(order, address, user, order.getDetails(), dispatch, buildDetailMaps(dispatch));
    }

    public OrderResponse toResponse(Order order, CustomerAddress address, User user,
                                    List<OrderDetail> details, Dispatch dispatch) {
        return toResponse(order, address, user, details, dispatch, new DispatchDetailMaps());
    }

    private OrderResponse toResponse(Order order, CustomerAddress address, User user,
                                     List<OrderDetail> details, Dispatch dispatch, DispatchDetailMaps maps) {
        if (details == null) details = Collections.emptyList();

        String phone = order.getCustomer().getPhone();
        String addrStr = order.getCustomer().getAddress();
        String cityName = null;
        String departmentName = null;
        String coordinatorName = null;
        String approvedByName = null;
        String dispatchUserName = null;
        String dispatchDriverName = null;
        String dispatchDriverDocument = null;
        String dispatchDriverPhone = null;
        String dispatchVehicleNumber = null;
        LocalDateTime dispatchDate = null;

        if (address != null && address.getCity() != null) {
            cityName = address.getCity().getName();
            if (address.getCity().getDepartment() != null) {
                departmentName = address.getCity().getDepartment().getName();
            }
        }
        if (user != null) {
            coordinatorName = (user.getFirstName() + " " + user.getLastName()).trim();
        }
        if (order.getApprovedById() != null) {
            User approver = userRepository.findById(order.getApprovedById()).orElse(null);
            if (approver != null) {
                approvedByName = (approver.getFirstName() + " " + approver.getLastName()).trim();
            }
        }

        if (dispatch != null) {
            if (dispatch.getDriver() != null) {
                dispatchDriverName = dispatch.getDriver().getName();
                dispatchDriverDocument = dispatch.getDriver().getDocument();
                dispatchDriverPhone = dispatch.getDriver().getPhone();
            }
            if (org.springframework.util.StringUtils.hasText(dispatch.getVehiclePlate())) {
                dispatchVehicleNumber = dispatch.getVehiclePlate().trim();
            } else if (dispatch.getVehicle() != null) {
                dispatchVehicleNumber = dispatch.getVehicle().getVehicleNumber();
            }
            if (dispatch.getUserId() != null) {
                User dispatchUser = userRepository.findById(dispatch.getUserId()).orElse(null);
                dispatchUserName = dispatchUser != null ? (dispatchUser.getFirstName() + " " + dispatchUser.getLastName()).trim() : null;
            }
            dispatchDate = dispatch.getDispatchDate();
        }

        // Si el pesoTotalCargue guardado en BD es null o 0 (pedidos creados antes de configurar
        // pesos en productos), se recalcula dinámicamente desde los detalles para garantizar
        // consistencia entre entornos local y producción.
        BigDecimal pesoTotalCargue = order.getPesoTotalCargue();
        if (pesoTotalCargue == null || pesoTotalCargue.compareTo(BigDecimal.ZERO) == 0) {
            pesoTotalCargue = details.stream()
                    .map(d -> {
                        BigDecimal unitWeight = BigDecimal.ZERO;
                        if (d.getProduct().getPesoUnidad() != null
                                && d.getProduct().getPesoUnidad().compareTo(BigDecimal.ZERO) > 0) {
                            unitWeight = d.getProduct().getPesoUnidad();
                        } else if (d.getProduct().getPresentation() != null && d.getProduct().getPresentation() > 0
                                && d.getProduct().getWeightGrams() != null && d.getProduct().getWeightGrams() > 0) {
                            unitWeight = BigDecimal.valueOf((long) d.getProduct().getPresentation() * d.getProduct().getWeightGrams())
                                    .divide(BigDecimal.valueOf(1000), 4, java.math.RoundingMode.HALF_UP);
                        }
                        return unitWeight.multiply(d.getQuantity() != null ? d.getQuantity() : BigDecimal.ZERO);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
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
                .approvedById(order.getApprovedById())
                .approvedByName(approvedByName)
                .approvedAt(order.getApprovedAt())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .total(order.getTotal())
                .pesoTotalCargue(pesoTotalCargue)
                .notes(order.getNotes())
                .dispatchUserName(dispatchUserName)
                .dispatchDriverName(dispatchDriverName)
                .dispatchDriverDocument(dispatchDriverDocument)
                .dispatchDriverPhone(dispatchDriverPhone)
                .dispatchVehicleNumber(dispatchVehicleNumber)
                .dispatchDate(dispatchDate)
                .details(details.stream().map(d -> toDetailResponse(d, maps.detalleProductoPorProducto, maps.deliveredPorProducto, maps.observationsPorProducto, maps.lotePorProducto)).collect(Collectors.toList()))
                .createdAt(order.getCreatedAt())
                .build();
    }

    private DispatchDetailMaps buildDetailMaps(Dispatch dispatch) {
        DispatchDetailMaps maps = new DispatchDetailMaps();
        if (dispatch == null || dispatch.getDetails() == null) return maps;
        dispatch.getDetails().forEach(dd -> {
            if (dd.getProduct() == null) return;
            Long pid = dd.getProduct().getId();
            if (dd.getDetalleProducto() != null && !dd.getDetalleProducto().isBlank()) {
                maps.detalleProductoPorProducto.put(pid, dd.getDetalleProducto());
            }
            if (dd.getDelivered() != null) {
                maps.deliveredPorProducto.put(pid, dd.getDelivered());
            }
            if (dd.getObservations() != null && !dd.getObservations().isBlank()) {
                maps.observationsPorProducto.put(pid, dd.getObservations());
            }
            if (dd.getLote() != null && !dd.getLote().isBlank()) {
                maps.lotePorProducto.put(pid, dd.getLote());
            }
        });
        return maps;
    }

    private static final class DispatchDetailMaps {
        final Map<Long, String> detalleProductoPorProducto = new HashMap<>();
        final Map<Long, BigDecimal> deliveredPorProducto = new HashMap<>();
        final Map<Long, String> observationsPorProducto = new HashMap<>();
        final Map<Long, String> lotePorProducto = new HashMap<>();
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
                .presentation(detail.getProduct().getPresentation())
                .weightGrams(detail.getProduct().getWeightGrams())
                .build();
    }
}
