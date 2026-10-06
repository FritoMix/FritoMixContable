-- =============================================================================
-- V66: Agregar columna de imagen (base64/url) a los productos
-- =============================================================================

ALTER TABLE products ADD COLUMN image TEXT;
