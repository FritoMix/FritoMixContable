-- =============================================================================
-- V64: DESPACHADOR3 puede leer pedidos, productos, vehículos y conductores
-- para realizar el cargue del camión (el formulario de despacho los necesita)
-- =============================================================================

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DESPACHADOR3'
  AND p.name IN ('ORDERS_VIEW', 'PRODUCTS_VIEW', 'VEHICLES_VIEW', 'DRIVERS_VIEW',
                 'NOTIFICATIONS_VIEW', 'NOTIFICATIONS_MARK_READ')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );