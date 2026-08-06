-- =============================================================================
-- V38: Soporte para despachos consolidados (pedido_multipedido)
--  - Agrega columna tipo_pedido a dispatches
--  - Permite que un despacho se relacione con N pedidos (uno por cliente)
--    mediante la tabla intermedia dispatch_orders
--  - Migra los despachos existentes (pedido_unico) a la nueva relación
-- =============================================================================

-- 1. Columna tipo_pedido: 'pedido_unico' (default) | 'pedido_multipedido'
ALTER TABLE dispatches ADD COLUMN tipo_pedido VARCHAR(20) NOT NULL DEFAULT 'pedido_unico';

-- 2. order_id pasa a ser opcional: en multipedido el despacho no tiene un único pedido
ALTER TABLE dispatches ALTER COLUMN order_id DROP NOT NULL;

-- 3. Eliminar la restricción UNIQUE sobre order_id (columna UNIQUE de V12)
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'dispatches_order_id_key') THEN
        ALTER TABLE dispatches DROP CONSTRAINT dispatches_order_id_key;
    END IF;
END $$;

-- 4. Tabla intermedia despacho <-> pedidos (N a N)
CREATE TABLE dispatch_orders (
    dispatch_id BIGINT NOT NULL REFERENCES dispatches(id) ON DELETE CASCADE,
    order_id    BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    PRIMARY KEY (dispatch_id, order_id)
);

-- 5. Migrar despachos existentes: cada order_id pasa a la tabla intermedia
INSERT INTO dispatch_orders (dispatch_id, order_id)
SELECT id, order_id FROM dispatches WHERE order_id IS NOT NULL;

COMMIT;
