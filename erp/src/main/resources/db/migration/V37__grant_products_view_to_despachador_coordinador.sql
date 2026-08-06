-- =============================================================================
-- V37: Conceder permisos de lectura adicionales a DESPACHADOR y COORDINADOR
-- Para que puedan crear despachos: necesitan ver pedidos y productos
-- =============================================================================

-- DESPACHADOR: necesita ver productos para crear despachos con detalles
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DESPACHADOR'
  AND p.name = 'PRODUCTS_VIEW'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- DESPACHADOR: necesita ver pedidos para seleccionarlos al despachar
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'DESPACHADOR'
  AND p.name = 'ORDERS_VIEW'
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );

-- COORDINADOR: ya tiene permisos de productos/pedidos, asegurar lectura
INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'COORDINADOR'
  AND p.name IN ('PRODUCTS_VIEW', 'ORDERS_VIEW')
  AND NOT EXISTS (
    SELECT 1 FROM role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );