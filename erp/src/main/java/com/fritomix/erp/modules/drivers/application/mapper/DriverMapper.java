package com.fritomix.erp.modules.drivers.application.mapper;

import com.fritomix.erp.modules.drivers.application.dto.response.DriverResponse;
import com.fritomix.erp.modules.drivers.domain.entity.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public DriverResponse toResponse(Driver driver) {
        return DriverResponse.builder()
                .id(driver.getId())
                .document(driver.getDocument())
                .name(driver.getName())
                .phone(driver.getPhone())
                .licenseNumber(driver.getLicenseNumber())
                .active(driver.getActive())
                .createdAt(driver.getCreatedAt())
                .build();
    }
}
