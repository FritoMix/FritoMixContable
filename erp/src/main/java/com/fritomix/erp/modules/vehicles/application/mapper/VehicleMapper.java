package com.fritomix.erp.modules.vehicles.application.mapper;

import com.fritomix.erp.modules.vehicles.application.dto.response.VehicleResponse;
import com.fritomix.erp.modules.vehicles.domain.entity.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {

    public VehicleResponse toResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .type(vehicle.getType())
                .capacity(vehicle.getCapacity())
                .dimension(vehicle.getDimension())
                .active(vehicle.getActive())
                .createdAt(vehicle.getCreatedAt())
                .build();
    }
}
