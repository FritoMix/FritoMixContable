package com.fritomix.erp.modules.customers.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CustomerRequest(
        @NotBlank @Size(max = 50)
        String document,

        @NotBlank @Size(max = 150)
        String businessName,

        @Size(max = 150)
        String contactName,

        @Size(max = 30)
        String phone,

        @Size(max = 150)
        String email,

        @Size(max = 250)
        String address,

        @NotNull
        Long cityId,

        Boolean active
) {
}
