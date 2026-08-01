CREATE TABLE roles
(
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(50) UNIQUE NOT NULL,

    description VARCHAR(150),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO roles(name, description)
VALUES
('ADMIN','Administrador del sistema'),
('CONTADOR','Área contable'),
('COORDINADOR','Coordinador de pedidos'),
('DESPACHADOR','Área de despacho');