-- =============================================================================
-- V55: Tabla de códigos para restablecer contraseña (flujo "olvidé mi contraseña")
-- =============================================================================

CREATE TABLE password_reset_codes (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    code_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_password_reset_codes_email ON password_reset_codes (email);
