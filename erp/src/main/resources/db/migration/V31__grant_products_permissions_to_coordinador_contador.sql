-- =============================================================================
-- V31: Conceder permisos de productos a los roles COORDINADOR y CONTADOR
-- =============================================================================

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name IN ('COORDINADOR', 'CONTADOR') AND p.name LIKE 'PRODUCTS_%'
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);
