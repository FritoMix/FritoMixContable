CREATE TABLE dispatches (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    driver_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    dispatch_number VARCHAR(50) NOT NULL UNIQUE,
    dispatch_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(30) NOT NULL DEFAULT 'READY',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dispatches_orders
        FOREIGN KEY (order_id) REFERENCES orders(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_dispatches_drivers
        FOREIGN KEY (driver_id) REFERENCES drivers(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_dispatches_vehicles
        FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
        ON DELETE RESTRICT
);

CREATE TABLE dispatch_details (
    id BIGSERIAL PRIMARY KEY,
    dispatch_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity NUMERIC(18,2) NOT NULL,
    delivered NUMERIC(18,2) NOT NULL DEFAULT 0,
    observations TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dispatch_details_dispatches
        FOREIGN KEY (dispatch_id) REFERENCES dispatches(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_dispatch_details_products
        FOREIGN KEY (product_id) REFERENCES products(id)
        ON DELETE RESTRICT
);
