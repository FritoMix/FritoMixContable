ALTER TABLE dispatches ADD COLUMN IF NOT EXISTS despachador_user_id BIGINT;
ALTER TABLE dispatches ADD COLUMN IF NOT EXISTS confirmado_por_user_id BIGINT;
ALTER TABLE dispatches ADD COLUMN IF NOT EXISTS placa_observacion TEXT;