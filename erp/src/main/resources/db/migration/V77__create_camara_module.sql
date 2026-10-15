-- ============================================================
-- V77: Módulo de Cámaras — Tablas principales
-- ============================================================

-- 1. Verificaciones de cámaras (una por despacho)
CREATE TABLE camara_verificaciones (
    id                          BIGSERIAL PRIMARY KEY,
    dispatch_id                 BIGINT NOT NULL REFERENCES dispatches(id) ON DELETE RESTRICT,
    estado_camaras              VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_CAMARAS',
    verificado_por_user_id      BIGINT REFERENCES users(id) ON DELETE SET NULL,
    aprobado_por_user_id        BIGINT REFERENCES users(id) ON DELETE SET NULL,
    fecha_inicio_verificacion   TIMESTAMP,
    fecha_aprobacion            TIMESTAMP,
    observaciones_generales     TEXT,
    version                     BIGINT NOT NULL DEFAULT 0,
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_camara_dispatch UNIQUE (dispatch_id)
);

-- 2. Detalle de verificación por línea de producto
CREATE TABLE camara_verificacion_detalles (
    id                   BIGSERIAL PRIMARY KEY,
    verificacion_id      BIGINT NOT NULL REFERENCES camara_verificaciones(id) ON DELETE CASCADE,
    dispatch_detail_id   BIGINT NOT NULL REFERENCES dispatch_details(id) ON DELETE RESTRICT,
    cantidad_despachada  NUMERIC(18,2) NOT NULL,
    cantidad_verificada  NUMERIC(18,2),
    estado_linea         VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    tipo_inconsistencia  VARCHAR(50),
    observacion_linea    TEXT,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Historial de eventos (append-only, sin updated_at)
CREATE TABLE camara_historial (
    id               BIGSERIAL PRIMARY KEY,
    verificacion_id  BIGINT NOT NULL REFERENCES camara_verificaciones(id) ON DELETE CASCADE,
    tipo_evento      VARCHAR(50) NOT NULL,
    user_id          BIGINT REFERENCES users(id) ON DELETE SET NULL,
    nombre_usuario   VARCHAR(200),
    estado_anterior  VARCHAR(30),
    estado_nuevo     VARCHAR(30),
    observacion      TEXT,
    metadata         TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Índices sobre columnas realmente consultadas
CREATE INDEX idx_camara_verif_dispatch   ON camara_verificaciones(dispatch_id);
CREATE INDEX idx_camara_verif_estado     ON camara_verificaciones(estado_camaras);
CREATE INDEX idx_camara_verif_created    ON camara_verificaciones(created_at DESC);
CREATE INDEX idx_camara_verif_aprobado   ON camara_verificaciones(aprobado_por_user_id);
CREATE INDEX idx_camara_det_verif        ON camara_verificacion_detalles(verificacion_id);
CREATE INDEX idx_camara_hist_verif       ON camara_historial(verificacion_id);
CREATE INDEX idx_camara_hist_created     ON camara_historial(created_at DESC);
