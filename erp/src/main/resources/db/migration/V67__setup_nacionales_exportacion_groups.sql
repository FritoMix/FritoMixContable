-- ============================================================
-- V67: Set Productos Nacionales and Productos Exportacion as main groups
-- ============================================================

BEGIN;

-- 1. Create main groups if they don't exist
INSERT INTO categories (name, description, parent_id) VALUES
('Productos Nacionales', 'Categoría principal para productos de distribución y consumo nacional', NULL),
('Productos Exportación', 'Categoría principal para productos de exportación e internacionales', NULL)
ON CONFLICT (name) DO NOTHING;

-- 2. Move all existing top-level categories/groups to be subcategories under 'Productos Nacionales'
UPDATE categories 
SET parent_id = (SELECT id FROM categories WHERE name = 'Productos Nacionales')
WHERE parent_id IS NULL 
  AND name NOT IN ('Productos Nacionales', 'Productos Exportación');

-- 3. Ensure any existing subcategories also point directly to 'Productos Nacionales'
UPDATE categories
SET parent_id = (SELECT id FROM categories WHERE name = 'Productos Nacionales')
WHERE parent_id IS NOT NULL 
  AND parent_id NOT IN (SELECT id FROM categories WHERE parent_id IS NULL);

COMMIT;
