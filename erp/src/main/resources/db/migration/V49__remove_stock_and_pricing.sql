-- ============================================================
-- V49: Eliminación de stock y precios (no requeridos por el negocio)
-- products: se eliminan price, cost, stock y version (bloqueo
-- optimista de stock).
-- order_details: se eliminan price y subtotal.
-- NOTA: orders.total se conserva como "total de bultos/unidades"
-- del pedido (se calcula server-side desde los detalles).
-- ============================================================

ALTER TABLE products DROP COLUMN price;
ALTER TABLE products DROP COLUMN cost;
ALTER TABLE products DROP COLUMN stock;
ALTER TABLE products DROP COLUMN version;

ALTER TABLE order_details DROP COLUMN price;
ALTER TABLE order_details DROP COLUMN subtotal;