package com.fritomix.erp.modules.auth.application.command;

import lombok.Builder;

@Builder
public record LoginCommand(
        String email,
        String password
) {
}
