-- Corrección del doble prefijo en permisos de multipedidos.
-- V71 insertó los permisos como 'PERMISSION_MULTIPEDIDOS_*', pero el
-- JwtAuthenticationFilter antepone 'PERMISSION_' al nombre almacenado,
-- lo que producía 'PERMISSION_PERMISSION_MULTIPEDIDOS_*' y nunca
-- coincidía con las autoridades exigidas por MultipedidoController.
-- Se renombran a la convención del resto de permisos (sin prefijo).
UPDATE permissions SET name = 'MULTIPEDIDOS_VIEW'
 WHERE name = 'PERMISSION_MULTIPEDIDOS_VIEW';

UPDATE permissions SET name = 'MULTIPEDIDOS_CREATE'
 WHERE name = 'PERMISSION_MULTIPEDIDOS_CREATE';

UPDATE permissions SET name = 'MULTIPEDIDOS_EDIT'
 WHERE name = 'PERMISSION_MULTIPEDIDOS_EDIT';

UPDATE permissions SET name = 'MULTIPEDIDOS_DELETE'
 WHERE name = 'PERMISSION_MULTIPEDIDOS_DELETE';