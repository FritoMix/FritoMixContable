-- ============================================================
-- V46: Reesquematizar vehículos (Nº vehículo, Tipo, kg, dimensión)
-- Fuente: vehiculos.sql (7 registros)
-- ============================================================

-- 1. Añadir columnas nuevas
ALTER TABLE vehicles ADD COLUMN vehicle_number VARCHAR(20);
ALTER TABLE vehicles ADD COLUMN type VARCHAR(100);
ALTER TABLE vehicles ADD COLUMN dimension NUMERIC(18,2);

-- 2. Actualizar los vehículos referenciados por despachos (ids 1, 6, 12, 32)
--    con los primeros 4 registros del archivo, conservando su id.
UPDATE vehicles SET vehicle_number='VEH-001', type='sencillo',        capacity=9500,  dimension=11.5 WHERE id = 1;
UPDATE vehicles SET vehicle_number='VEH-002', type='con remolque',    capacity=17500, dimension=24   WHERE id = 6;
UPDATE vehicles SET vehicle_number='VEH-003', type='tractomula',      capacity=35000, dimension=40   WHERE id = 12;
UPDATE vehicles SET vehicle_number='VEH-004', type='patineta',        capacity=17500, dimension=20   WHERE id = 32;

-- 3. Quitar las columnas antiguas y sus restricciones (para poder insertar)
ALTER TABLE vehicles DROP CONSTRAINT IF EXISTS vehicles_plate_key;
ALTER TABLE vehicles DROP COLUMN plate;
ALTER TABLE vehicles DROP COLUMN brand;
ALTER TABLE vehicles DROP COLUMN model;

-- 4. Insertar los 3 registros restantes
INSERT INTO vehicles (vehicle_number, type, capacity, dimension, active) VALUES
    ('VEH-005', 'turbo',        4500,  9,   true),
    ('VEH-006', 'doble troque', 18000, 20,  true),
    ('VEH-007', 'sencillo ruta',9500,  11,  true);

-- 5. Eliminar los vehículos sobrantes (no referenciados por despachos)
DELETE FROM vehicles WHERE vehicle_number IS NULL;

-- 6. Restricciones finales
ALTER TABLE vehicles ALTER COLUMN vehicle_number SET NOT NULL;
ALTER TABLE vehicles ALTER COLUMN type SET NOT NULL;
ALTER TABLE vehicles ALTER COLUMN dimension SET NOT NULL;
ALTER TABLE vehicles ADD CONSTRAINT vehicles_vehicle_number_key UNIQUE (vehicle_number);
