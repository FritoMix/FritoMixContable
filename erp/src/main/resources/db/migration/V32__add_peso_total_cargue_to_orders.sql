-- =============================================================================
-- V32: Agregar peso_total_cargue a la tabla orders
-- =============================================================================

ALTER TABLE orders
ADD COLUMN peso_total_cargue NUMERIC(18,4) DEFAULT 0 NOT NULL;
