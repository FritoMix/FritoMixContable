ALTER TABLE dispatches
    ADD CONSTRAINT fk_dispatches_despachador_user
        FOREIGN KEY (despachador_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL;

ALTER TABLE dispatches
    ADD CONSTRAINT fk_dispatches_confirmado_user
        FOREIGN KEY (confirmado_por_user_id)
        REFERENCES users(id)
        ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_dispatches_user_id
    ON dispatches (user_id);

CREATE INDEX IF NOT EXISTS idx_dispatches_confirmado_user
    ON dispatches (confirmado_por_user_id);

CREATE INDEX IF NOT EXISTS idx_dispatches_despachador_user
    ON dispatches (despachador_user_id);