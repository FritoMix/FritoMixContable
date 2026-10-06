-- Tabla de paquetes Multipedido
CREATE TABLE IF NOT EXISTS multipedidos (
    id          BIGSERIAL PRIMARY KEY,
    numero      VARCHAR(30)  NOT NULL UNIQUE,
    status      VARCHAR(30)  NOT NULL DEFAULT 'PENDIENTE',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Relación N:M entre multipedido y orders
CREATE TABLE IF NOT EXISTS multipedido_orders (
    multipedido_id BIGINT NOT NULL REFERENCES multipedidos(id) ON DELETE CASCADE,
    order_id       BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    PRIMARY KEY (multipedido_id, order_id)
);

-- Permisos para coordinador y admin
INSERT INTO permissions (name) VALUES
  ('PERMISSION_MULTIPEDIDOS_VIEW'),
  ('PERMISSION_MULTIPEDIDOS_CREATE'),
  ('PERMISSION_MULTIPEDIDOS_EDIT'),
  ('PERMISSION_MULTIPEDIDOS_DELETE')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name IN ('ROLE_COORDINADOR', 'ROLE_ADMIN')
  AND p.name IN (
    'PERMISSION_MULTIPEDIDOS_VIEW',
    'PERMISSION_MULTIPEDIDOS_CREATE',
    'PERMISSION_MULTIPEDIDOS_EDIT',
    'PERMISSION_MULTIPEDIDOS_DELETE'
  )
ON CONFLICT DO NOTHING;
