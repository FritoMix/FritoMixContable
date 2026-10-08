-- Bloqueo optimista (@Version) en orders y users
-- dispatches y products ya lo tienen desde V41.
ALTER TABLE orders ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE users ADD COLUMN version BIGINT NOT NULL DEFAULT 0;