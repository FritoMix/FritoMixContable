-- =============================================================================
-- V60: DESPACHADOR1 crea el despacho al asignar la placa del vehículo
-- =============================================================================

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DESPACHADOR1'
  AND p.name = 'DISPATCHES_CREATE'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );