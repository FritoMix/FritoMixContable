-- =============================================================================
-- V27: Conceder permisos de clientes al rol COORDINADOR
-- =============================================================================

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'COORDINADOR' AND p.name LIKE 'CUSTOMERS_%'
AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
);
