CREATE TABLE arrumes (
    id BIGSERIAL PRIMARY KEY,
    dispatch_id BIGINT NOT NULL REFERENCES dispatches(id) ON DELETE CASCADE,
    num_arrume INTEGER,
    arrume_producto VARCHAR(255),
    cantidad BIGINT,
    lote VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_arrumes_dispatch_id ON arrumes(dispatch_id);
