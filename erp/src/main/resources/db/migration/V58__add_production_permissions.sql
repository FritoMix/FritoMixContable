INSERT INTO permissions (name, description)
VALUES ('ORDERS_PRODUCTION', 'Confirmar estados de producción de los pedidos')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name IN ('PRODUCCION', 'ADMIN')
  AND p.name = 'ORDERS_PRODUCTION'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );