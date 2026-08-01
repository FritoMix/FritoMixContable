INSERT INTO permissions(name, description) VALUES
('CUSTOMERS_VIEW', 'Ver listado de clientes'),
('CUSTOMERS_CREATE', 'Crear nuevos clientes'),
('CUSTOMERS_EDIT', 'Editar clientes existentes'),
('CUSTOMERS_DELETE', 'Eliminar clientes');

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN' AND p.name LIKE 'CUSTOMERS_%';
