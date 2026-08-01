-- ============================================================
-- V26: Permisos de pedidos y despachos
-- ============================================================

INSERT INTO permissions(name, description) VALUES
('DISPATCHES_VIEW', 'Ver listado de despachos'),
('DISPATCHES_CREATE', 'Crear despachos'),
('DISPATCHES_EDIT', 'Editar despachos'),
('DISPATCHES_DELETE', 'Eliminar despachos')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name LIKE 'DISPATCHES_%'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
