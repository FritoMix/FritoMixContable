-- ============================================================
-- V70: Add tipo_pedido column to orders table
-- ============================================================

ALTER TABLE orders ADD COLUMN IF NOT EXISTS tipo_pedido VARCHAR(30) NOT NULL DEFAULT 'pedido_unico';
