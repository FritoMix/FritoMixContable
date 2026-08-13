-- ============================================================
-- V48: Desbloqueo automático de cuentas bloqueadas
-- Agrega locked_until a users: ventana de bloqueo temporal.
-- Agrega lock_duration_minutes a company_settings: duración
-- configurable de la ventana de bloqueo (default 15 min).
-- ============================================================

ALTER TABLE users
    ADD COLUMN locked_until TIMESTAMP;

ALTER TABLE company_settings
    ADD COLUMN lock_duration_minutes INTEGER NOT NULL DEFAULT 15;