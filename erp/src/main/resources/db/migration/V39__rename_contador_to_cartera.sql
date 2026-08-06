-- V39: Renombrar rol CONTADOR a CARTERA

-- Actualizar el rol
UPDATE roles 
SET name = 'CARTERA', 
    description = 'Área de cartera',
    updated_at = CURRENT_TIMESTAMP
WHERE name = 'CONTADOR';