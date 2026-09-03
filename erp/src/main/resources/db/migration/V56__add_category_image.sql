-- =============================================================================
-- V56: Agregar columna de imagen (base64) a las categorías/grupos
-- =============================================================================

ALTER TABLE categories ADD COLUMN image TEXT;
