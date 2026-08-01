package com.fritomix.erp.modules.dispatch.application.mapper;

import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse;
import com.fritomix.erp.modules.dispatch.application.dto.response.DispatchResponse.DispatchDetailResponse;
import com.fritomix.erp.modules.dispatch.domain.entity.Dispatch;
import com.fritomix.erp.modules.dispatch.domain.entity.DispatchDetail;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import com.fritomix.erp.modules.orders.domain.entity.Order;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DispatchMapper {

    private final UserRepository userRepository;

    public DispatchResponse toResponse(Dispatch dispatch) {
        List<DispatchDetail> details = dispatch.getDetails();
        if (details == null) details = Collections.emptyList();

        String dispatchUserName = null;
        if (dispatch.getUserId() != null) {
            dispatchUserName = userRepository.findById(dispatch.getUserId())
                    .map(u -> u.getFirstName() + " " + u.getLastName())
                    .orElse(null);
        }

        Order order = dispatch.getOrder();
        Driver driver = dispatch.getDriver();
        Vehicle vehicle = dispatch.getVehicle();

        DispatchResponse.DispatchResponseBuilder builder = DispatchResponse.builder()
                .id(dispatch.getId())
                .dispatchNumber(dispatch.getDispatchNumber())
                .dispatchDate(dispatch.getDispatchDate())
                .status(dispatch.getStatus())
                .notes(dispatch.getNotes())
                .dispatchUserName(dispatchUserName)
                .details(details.stream().map(this::toDetailResponse).collect(Collectors.toList()))
                .createdAt(dispatch.getCreatedAt());

        if (order != null) {
            builder.orderId(order.getId())
                   .orderNumber(order.getOrderNumber())
                   .pesoTotalCargue(order.getPesoTotalCargue());
        }
        if (driver != null) {
            builder.driverId(driver.getId())
                   .driverName(driver.getName())
                   .driverDocument(driver.getDocument());
        }
        if (vehicle != null) {
            builder.vehicleId(vehicle.getId())
                   .vehiclePlate(vehicle.getPlate())
                   .vehicleBrand(vehicle.getBrand())
                   .vehicleModel(vehicle.getModel());
        }

        return builder.build();
    }

    private DispatchDetailResponse toDetailResponse(DispatchDetail detail) {
        DispatchDetailResponse.DispatchDetailResponseBuilder builder = DispatchDetailResponse.builder()
                .id(detail.getId())
                .quantity(detail.getQuantity())
                .delivered(detail.getDelivered())
                .observations(detail.getObservations());

        if (detail.getProduct() != null) {
            builder.productId(detail.getProduct().getId())
                   .productName(detail.getProduct().getName())
                   .productCode(detail.getProduct().getCode());
        }

        return builder.build();
    }
}
