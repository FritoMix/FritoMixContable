package com.fritomix.erp.modules.dispatch.application.dto.response;

import lombok.Builder;

@Builder
public record DespachadorDto(
        Long id,
        String nombre,
        String email
) {
}