CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(200)
);

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

INSERT INTO permissions(name, description) VALUES
('USERS_VIEW', 'Ver listado de usuarios'),
('USERS_CREATE', 'Crear nuevos usuarios'),
('USERS_EDIT', 'Editar usuarios existentes'),
('USERS_DELETE', 'Eliminar usuarios'),
('USERS_MANAGE_STATUS', 'Activar/desactivar usuarios'),
('ROLES_VIEW', 'Ver roles'),
('ROLES_CREATE', 'Crear roles'),
('ROLES_EDIT', 'Editar roles'),
('ROLES_DELETE', 'Eliminar roles'),
('ROLES_MANAGE_PERMISSIONS', 'Gestionar permisos de roles'),
('PRODUCTS_VIEW', 'Ver productos'),
('PRODUCTS_CREATE', 'Crear productos'),
('PRODUCTS_EDIT', 'Editar productos'),
('PRODUCTS_DELETE', 'Eliminar productos'),
('ORDERS_VIEW', 'Ver pedidos'),
('ORDERS_CREATE', 'Crear pedidos'),
('ORDERS_EDIT', 'Editar pedidos'),
('ORDERS_DELETE', 'Eliminar pedidos');

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN';

INSERT INTO users (
    role_id,
    first_name,
    last_name,
    email,
    password,
    enabled,
    account_non_locked,
    account_non_expired,
    credentials_non_expired,
    created_at,
    updated_at
)
VALUES (
    (SELECT id FROM roles WHERE name = 'ADMIN'),
    'Ferney',
    'Ipiales',
    'admin@fritomix.com',
    '$2a$10$3iHbwbAvPmr.PbXfJrCLpeSiigVwE9Oxr5NbLJtUGpXB82uV2PnYG',
    true,
    true,
    true,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
