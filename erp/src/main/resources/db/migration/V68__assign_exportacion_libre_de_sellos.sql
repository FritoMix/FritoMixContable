-- ============================================================
-- V68: Assign 'Libre de Sellos' subcategories to Productos Exportación
-- ============================================================

BEGIN;

-- Assign all subcategories containing 'SELLOS' to 'Productos Exportación'
UPDATE categories
SET parent_id = (SELECT id FROM categories WHERE name = 'Productos Exportación')
WHERE (name ILIKE '%SELLOS%' OR name ILIKE '%EXPORT%')
  AND name NOT IN ('Productos Nacionales', 'Productos Exportación');

COMMIT;
