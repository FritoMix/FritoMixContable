-- ============================================================
-- V23: Agregar permisos de conductores
-- ============================================================

INSERT INTO permissions(name, description) VALUES
('DRIVERS_VIEW', 'Ver listado de conductores'),
('DRIVERS_CREATE', 'Crear conductores'),
('DRIVERS_EDIT', 'Editar conductores'),
('DRIVERS_DELETE', 'Eliminar conductores')
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions(name, description) VALUES
('VEHICLES_VIEW', 'Ver listado de vehículos'),
('VEHICLES_CREATE', 'Crear vehículos'),
('VEHICLES_EDIT', 'Editar vehículos'),
('VEHICLES_DELETE', 'Eliminar vehículos')
ON CONFLICT (name) DO NOTHING;

-- Asignar todos los permisos de conductores y vehículos al rol ADMIN
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name LIKE 'DRIVERS_%'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- Asignar permisos de vehículos al rol ADMIN
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name LIKE 'VEHICLES_%'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
