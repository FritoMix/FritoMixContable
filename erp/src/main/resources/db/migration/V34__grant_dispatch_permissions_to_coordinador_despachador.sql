-- =============================================================================
-- V34: Conceder permisos de despachos a los roles COORDINADOR y DESPACHADOR
-- =============================================================================

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name IN ('COORDINADOR', 'DESPACHADOR')
  AND p.name LIKE 'DISPATCHES_%'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
