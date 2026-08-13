-- ============================================================
-- V47: Enforcement de política de seguridad en users
-- Agrega contador de intentos fallidos para habilitar el
-- bloqueo por max_login_attempts de company_settings.
-- ============================================================

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS failed_attempts INT NOT NULL DEFAULT 0;

UPDATE users SET failed_attempts = 0 WHERE failed_attempts IS NULL;