-- =============================================================================
-- V43: Lotes por detalle de despacho
--  - Agrega lote a dispatch_details: lotes del producto que se muestran en la
--    columna LOTE del PDF del pedido.
-- =============================================================================

ALTER TABLE dispatch_details ADD COLUMN lote VARCHAR(100);

COMMIT;
