-- ============================================================
-- V52: Suscripciones Web Push por usuario.
-- Permite guardar las suscripciones Push del navegador de cada
-- usuario para enviar notificaciones push de nuevos pedidos.
-- ============================================================

CREATE TABLE push_subscriptions (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    endpoint     VARCHAR(2048) NOT NULL,
    p256dh       VARCHAR(255)  NOT NULL,
    auth         VARCHAR(255)  NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_push_subscription_endpoint UNIQUE (endpoint)
);

CREATE INDEX idx_push_subscriptions_user ON push_subscriptions(user_id);
