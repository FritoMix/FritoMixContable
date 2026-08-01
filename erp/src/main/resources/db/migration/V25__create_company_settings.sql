-- ============================================================
-- V25: Configuración de empresa
-- ============================================================

CREATE TABLE company_settings (
    id BIGSERIAL PRIMARY KEY,
    company_name VARCHAR(200) NOT NULL,
    nit VARCHAR(50),
    admin_email VARCHAR(150),

    -- Empresa
    address VARCHAR(250),
    phone VARCHAR(30),
    city VARCHAR(100),
    department VARCHAR(100),
    economic_activity VARCHAR(200),

    -- Seguridad
    password_min_length INT NOT NULL DEFAULT 8,
    password_require_special BOOLEAN NOT NULL DEFAULT TRUE,
    password_expiration_days INT NOT NULL DEFAULT 90,
    session_timeout_minutes INT NOT NULL DEFAULT 60,
    max_login_attempts INT NOT NULL DEFAULT 5,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO company_settings (company_name, nit, admin_email, address, phone, city, department, economic_activity)
VALUES ('FritoMix S.A.S', '900.123.456-7', 'admin@fritomix.com', 'CRA 5 # 21-45', '3101234567', 'IBAGUÉ', 'TOLIMA', 'Comercialización de productos alimenticios');

INSERT INTO permissions(name, description) VALUES
('SETTINGS_VIEW', 'Ver configuración de empresa'),
('SETTINGS_EDIT', 'Editar configuración de empresa')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions(role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.name = 'ADMIN'
  AND p.name LIKE 'SETTINGS_%'
  AND NOT EXISTS (
      SELECT 1 FROM role_permissions rp
      WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
