-- ============================================================
-- V69: Organize Productos Nacionales subcategories (Bebidas, Extruido, Galletas, Panadería, Papa, Pelet, Platano)
-- ============================================================

BEGIN;

-- 1. Ensure the 7 main category cards exist under 'Productos Nacionales'
INSERT INTO categories (name, description, parent_id) VALUES
('Bebidas', 'Bebidas y refrescos', (SELECT id FROM categories WHERE name = 'Productos Nacionales')),
('Extruido', 'Snacks extruidos', (SELECT id FROM categories WHERE name = 'Productos Nacionales')),
('Galletas', 'Galletas y dulces', (SELECT id FROM categories WHERE name = 'Productos Nacionales')),
('Panadería', 'Productos de panadería', (SELECT id FROM categories WHERE name = 'Productos Nacionales')),
('Papa', 'Snacks de papa', (SELECT id FROM categories WHERE name = 'Productos Nacionales')),
('Pelet', 'Snacks tipo pelet', (SELECT id FROM categories WHERE name = 'Productos Nacionales')),
('Platano', 'Snacks de plátano', (SELECT id FROM categories WHERE name = 'Productos Nacionales'))
ON CONFLICT (name) DO UPDATE 
SET parent_id = (SELECT id FROM categories WHERE name = 'Productos Nacionales');

-- 2. Move subcategories under their respective parent category
UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Papa')
WHERE name IN ('Tradicional', 'Nachos & Totopos', 'Frutos Secos', 'Maíz', 'Maní', 'Otros', 'PT PAPA');

UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Pelet')
WHERE name IN ('Granos & Snacks', 'PT PELLET', 'PT HARINAS', 'PT MEZCLAS');

UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Galletas')
WHERE name IN ('Dulces', 'GALLETAS 19%');

UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Bebidas')
WHERE name IN ('Surtido Bebidas', 'BEBIDAS');

UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Panadería')
WHERE name = 'PANADERIA';

UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Extruido')
WHERE name = 'PT EXTRUIDOS';

UPDATE categories SET parent_id = (SELECT id FROM categories WHERE name = 'Platano')
WHERE name = 'PT PLATANO';

COMMIT;
