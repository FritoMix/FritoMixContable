-- ============================================================
-- V50: Amplía la columna token de refresh_tokens.
-- Los JWT de refresh (~695 chars) superan el VARCHAR(500)
-- original, causando DataIntegrityViolationException (409).
-- ============================================================

ALTER TABLE refresh_tokens
    ALTER COLUMN token TYPE VARCHAR(2048);