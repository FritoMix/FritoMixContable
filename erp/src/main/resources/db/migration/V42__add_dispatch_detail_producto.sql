-- =============================================================================
-- V42: Detalle de producto por despacho (cómo se arruma el pedido)
--  - Agrega detalle_producto a dispatch_details: texto libre por producto con
--    las instrucciones de arrume que se muestran en el PDF del pedido.
-- =============================================================================

ALTER TABLE dispatch_details ADD COLUMN detalle_producto TEXT;

COMMIT;
