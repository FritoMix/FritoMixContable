-- Add numero_factura column to dispatches and dispatch_orders
ALTER TABLE dispatches ADD COLUMN numero_factura VARCHAR(50) NULL;
ALTER TABLE dispatch_orders ADD COLUMN numero_factura VARCHAR(50) NULL;
