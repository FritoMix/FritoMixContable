package com.fritomix.erp.modules.drivers.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record DriverRequest(
        @NotBlank @Size(max = 30)
        String document,

        @NotBlank @Size(max = 150)
        String name,

        @Size(max = 30)
        String phone,

        @NotBlank @Size(max = 50)
        String licenseNumber,

        Boolean active
) {
}
