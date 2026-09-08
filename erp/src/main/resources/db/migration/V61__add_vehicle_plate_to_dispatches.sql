-- =============================================================================
-- V61: placa de vehículo como campo libre (informativo) en despachos
-- =============================================================================

ALTER TABLE dispatches ADD COLUMN IF NOT EXISTS vehicle_plate VARCHAR(30);