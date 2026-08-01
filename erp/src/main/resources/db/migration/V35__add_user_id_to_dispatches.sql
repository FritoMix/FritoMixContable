-- =============================================================================
-- V35: Agregar user_id a dispatches para trackear quién creó el despacho
-- =============================================================================

ALTER TABLE dispatches ADD COLUMN IF NOT EXISTS user_id BIGINT REFERENCES users(id);
