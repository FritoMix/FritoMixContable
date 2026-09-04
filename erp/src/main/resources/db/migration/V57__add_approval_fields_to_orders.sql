-- =============================================================================
-- V57: Registro del aprobador del pedido (cartera)
-- =============================================================================

ALTER TABLE orders
    ADD COLUMN approved_by BIGINT,
    ADD COLUMN approved_at TIMESTAMP;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_approved_by FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX idx_orders_approved_by ON orders (approved_by);