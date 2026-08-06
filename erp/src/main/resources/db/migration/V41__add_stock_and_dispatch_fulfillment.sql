-- =============================================================================
-- V41: Módulo logístico de despacho
--  - Agrega stock a products (Integer) para el control de inventario
--  - Agrega version a products para bloqueo optimista al descontar stock
--  - Agrega cumplimiento a dispatches: COMPLETO | PARCIAL (independiente del
--    flujo de estado PENDIENTE/APROBADO/EN RUTA/FINALIZADO)
--  - Agrega version a dispatches para bloqueo optimista en actualizaciones
-- =============================================================================

-- 1. Stock de inventario por producto
ALTER TABLE products ADD COLUMN stock INTEGER NOT NULL DEFAULT 0;

-- 2. Bloqueo optimista (concurrencia al descontar stock)
ALTER TABLE products ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- 3. Cumplimiento del despacho (solicitado vs despachado)
ALTER TABLE dispatches ADD COLUMN cumplimiento VARCHAR(20);

-- 4. Bloqueo optimista sobre el despacho
ALTER TABLE dispatches ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

COMMIT;
