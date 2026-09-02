-- =============================================================================
-- V54: Nuevos roles de flujo operativo
-- PRODUCCION, CAMARA, FACTURACION, DESPACHADOR1, DESPACHADOR2, DESPACHADOR3
-- =============================================================================

INSERT INTO roles(name, description) VALUES
('PRODUCCION', 'Área de producción'),
('CAMARA', 'Revisión y aprobación de arrumes'),
('FACTURACION', 'Área de facturación'),
('DESPACHADOR1', 'Asignación de placa de vehículo'),
('DESPACHADOR2', 'Confirmación de placa y asignación de despachador'),
('DESPACHADOR3', 'Cargue de camión y arrume de pedido')
ON CONFLICT (name) DO NOTHING;

-- PRODUCCION: ver productos y pedidos (cantidad por producto)
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'PRODUCCION'
  AND p.name IN ('PRODUCTS_VIEW', 'ORDERS_VIEW')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- CAMARA: ver pedidos y aprobar estado
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'CAMARA'
  AND p.name IN ('ORDERS_VIEW', 'ORDERS_CHANGE_STATUS')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- FACTURACION: ver y editar pedidos (registrar número de factura)
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'FACTURACION'
  AND p.name IN ('ORDERS_VIEW', 'ORDERS_EDIT', 'DISPATCHES_VIEW', 'DISPATCHES_EDIT')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- DESPACHADOR1: asignar placa de vehículo disponible
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DESPACHADOR1'
  AND p.name IN ('DISPATCHES_VIEW', 'DISPATCHES_EDIT', 'DISPATCHES_CHANGE_STATUS', 'VEHICLES_VIEW')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- DESPACHADOR2: confirma placa y selecciona despachador
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DESPACHADOR2'
  AND p.name IN ('DISPATCHES_VIEW', 'DISPATCHES_EDIT', 'DISPATCHES_CHANGE_STATUS', 'VEHICLES_VIEW', 'DRIVERS_VIEW')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- DESPACHADOR3: ver pedidos que le asignaron, cargar y arrumar
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DESPACHADOR3'
  AND p.name IN ('DISPATCHES_VIEW', 'DISPATCHES_CHANGE_STATUS')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
