-- ============================================================
-- V33: Permisos de reportes
-- ============================================================

INSERT INTO permissions(name, description) VALUES
('REPORTS_VIEW', 'Ver reportes y descargar PDFs')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name LIKE 'REPORTS_%'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
