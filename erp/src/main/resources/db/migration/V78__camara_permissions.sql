-- ============================================================
-- V78: Módulo de Cámaras — Permisos y asignación a roles
-- ============================================================

-- 1. Crear permisos del módulo
INSERT INTO permissions(name, description) VALUES
('CAMARAS_VIEW',      'Ver bandeja y fichas de cámaras'),
('CAMARAS_INICIAR',   'Iniciar verificación en cámaras'),
('CAMARAS_VERIFICAR', 'Registrar cantidades verificadas'),
('CAMARAS_OBSERVAR',  'Registrar observación en ficha'),
('CAMARAS_APROBAR',   'Aprobar ficha de cámaras'),
('CAMARAS_RECHAZAR',  'Rechazar ficha de cámaras'),
('CAMARAS_REPORTE',   'Generar reporte Word de cámaras'),
('CAMARAS_HISTORIAL', 'Ver historial de auditoría de cámaras'),
('CAMARAS_ADMIN',     'Operaciones administrativas de cámaras (reabrir fichas)')
ON CONFLICT (name) DO NOTHING;

-- 2. Asegurar que el rol CAMARA existe
INSERT INTO roles(name, description)
VALUES ('CAMARA', 'Personal de Cámaras de almacenamiento')
ON CONFLICT (name) DO NOTHING;

-- 3. Permisos para CAMARA (verificar, aprobar, rechazar, reporte, historial)
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'CAMARA'
  AND p.name IN (
      'CAMARAS_VIEW', 'CAMARAS_INICIAR', 'CAMARAS_VERIFICAR',
      'CAMARAS_OBSERVAR', 'CAMARAS_APROBAR', 'CAMARAS_RECHAZAR',
      'CAMARAS_REPORTE', 'CAMARAS_HISTORIAL'
  )
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- 4. Permisos para ADMIN (todos)
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name LIKE 'CAMARAS_%'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- 5. DESPACHADOR roles: solo ver, reporte e historial
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name IN ('DESPACHADOR', 'DESPACHADOR1', 'DESPACHADOR2', 'DESPACHADOR3')
  AND p.name IN ('CAMARAS_VIEW', 'CAMARAS_REPORTE', 'CAMARAS_HISTORIAL')
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- 6. Agregar permiso CAMARAS_ENVIAR al módulo de Despacho (acción "Enviar a Cámaras")
INSERT INTO permissions(name, description) VALUES
('CAMARAS_ENVIAR', 'Enviar despacho a verificación de Cámaras')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name IN ('DESPACHADOR', 'DESPACHADOR1', 'DESPACHADOR2', 'DESPACHADOR3', 'ADMIN')
  AND p.name = 'CAMARAS_ENVIAR'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
