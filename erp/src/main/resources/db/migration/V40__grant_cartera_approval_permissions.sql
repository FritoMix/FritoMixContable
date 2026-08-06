-- =============================================================================
-- V40: Conceder a CARTERA permisos de aprobación de pedidos y despachos
-- CARTERA puede ver y cambiar el estado (aprobar/cancelar) de pedidos y despachos.
-- No recibe permisos de creación, edición ni eliminación.
-- =============================================================================

-- Crear permisos específicos de cambio de estado (si no existen)
INSERT INTO permissions(name, description) VALUES
('ORDERS_CHANGE_STATUS', 'Aprobar o cancelar pedidos'),
('DISPATCHES_CHANGE_STATUS', 'Aprobar o cancelar despachos')
ON CONFLICT (name) DO NOTHING;

-- ADMIN: permisos de cambio de estado
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name IN ('ORDERS_CHANGE_STATUS', 'DISPATCHES_CHANGE_STATUS')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- COORDINADOR: permisos de cambio de estado de pedidos y despachos
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'COORDINADOR'
  AND p.name IN ('ORDERS_CHANGE_STATUS', 'DISPATCHES_CHANGE_STATUS')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- DESPACHADOR: permisos de cambio de estado de despachos
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DESPACHADOR'
  AND p.name IN ('DISPATCHES_CHANGE_STATUS')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- CARTERA: ver y aprobar/cancelar pedidos y despachos
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'CARTERA'
  AND p.name IN ('ORDERS_VIEW', 'ORDERS_CHANGE_STATUS', 'DISPATCHES_VIEW', 'DISPATCHES_CHANGE_STATUS')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
