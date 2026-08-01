-- =============================================================================
-- V36: Corregir permisos por rol para flujo coherente
-- COORDINADOR: gestiona pedidos, pasa a despachador
-- DESPACHADOR: gestiona despachos, conductores, vehículos
-- CONTADOR: reportes
-- =============================================================================

-- COORDINADOR: permisos de pedidos (ORDERS)
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'COORDINADOR'
  AND p.name LIKE 'ORDERS_%'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- COORDINADOR: permisos de reportes (REPORTS_VIEW)
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'COORDINADOR'
  AND p.name = 'REPORTS_VIEW'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- DESPACHADOR: permisos de conductores (DRIVERS)
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DESPACHADOR'
  AND p.name LIKE 'DRIVERS_%'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- DESPACHADOR: permisos de vehículos (VEHICLES)
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DESPACHADOR'
  AND p.name LIKE 'VEHICLES_%'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- CONTADOR: permisos de reportes (REPORTS_VIEW)
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'CONTADOR'
  AND p.name = 'REPORTS_VIEW'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
