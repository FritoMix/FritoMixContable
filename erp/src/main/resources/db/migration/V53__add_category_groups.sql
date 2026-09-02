-- ============================================================
-- V53: Add 7 parent groups and map existing categories
-- ============================================================

BEGIN;

-- 1. Rename existing "Bebidas" category to avoid UNIQUE conflict with the new group
UPDATE categories SET name = 'Surtido Bebidas' WHERE name = 'Bebidas';

-- 2. Create 7 parent groups
INSERT INTO categories (name, description) VALUES
('Papa', 'Snacks de papa'),
('Platano', 'Snacks de plátano'),
('Pelet', 'Snacks tipo pelet'),
('Extruido', 'Snacks extruidos'),
('Galletas', 'Galletas y dulces'),
('Bebidas', 'Bebidas'),
('Panadería', 'Panadería')
ON CONFLICT (name) DO NOTHING;

-- 3. Map existing categories to their groups
UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Papa')
WHERE name IN ('Tradicional', 'Nachos & Totopos', 'Frutos Secos', 'Maíz', 'Maní', 'Otros');

UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Pelet')
WHERE name = 'Granos & Snacks';

UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Galletas')
WHERE name = 'Dulces';

UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Bebidas')
WHERE name = 'Surtido Bebidas';

COMMIT;
