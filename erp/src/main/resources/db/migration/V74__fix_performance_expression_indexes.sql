-- pg_trgm se crea como superusuario en docker/postgres/init/01-security-roles.sh


DROP INDEX IF EXISTS idx_customers_business_name_trgm;
DROP INDEX IF EXISTS idx_customers_document_trgm;
DROP INDEX IF EXISTS idx_products_name_trgm;

CREATE INDEX IF NOT EXISTS idx_customers_business_name_trgm
    ON customers USING gin (lower(business_name) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_customers_document_trgm
    ON customers USING gin (lower(document) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_products_name_trgm
    ON products USING gin (lower(name) gin_trgm_ops);