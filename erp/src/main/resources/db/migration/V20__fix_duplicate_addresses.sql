-- =============================================================================
-- V20: Eliminar direcciones duplicadas y agregar unique constraint
-- =============================================================================

BEGIN;

-- 1) Eliminar duplicados: conservar solo la dirección más antigua por cliente
DELETE FROM customer_addresses ca
WHERE ca.id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (
            PARTITION BY customer_id ORDER BY created_at ASC, id ASC
        ) AS rn
        FROM customer_addresses
    ) sub
    WHERE sub.rn > 1
);

-- 2) Agregar unique constraint para evitar futuros duplicados
CREATE UNIQUE INDEX uq_customer_main_address
ON customer_addresses (customer_id) WHERE is_main = TRUE;

COMMIT;
