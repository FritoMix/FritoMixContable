-- =============================================================================
-- V19: Insertar ciudades faltantes y reparar direcciones de clientes
-- =============================================================================
BEGIN;

-- 1) Insertar 112 ciudades faltantes
INSERT INTO cities (name, department_id)
SELECT 'Achí', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Anserma', d.id FROM departments d WHERE d.name ILIKE 'Caldas'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Ansermanuevo', d.id FROM departments d WHERE d.name ILIKE 'Valle del Cauca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Arenal', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Ariguaní', d.id FROM departments d WHERE d.name ILIKE 'Magdalena'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Armenia', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Baranoa', d.id FROM departments d WHERE d.name ILIKE 'Atlántico'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Barbosa', d.id FROM departments d WHERE d.name ILIKE 'Santander'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Becerril', d.id FROM departments d WHERE d.name ILIKE 'Cesar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Belén De Umbría', d.id FROM departments d WHERE d.name ILIKE 'Risaralda'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Bogotá, D.C.', d.id FROM departments d WHERE d.name ILIKE 'Cundinamarca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Buenavista', d.id FROM departments d WHERE d.name ILIKE 'Boyacá'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Caicedo', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Caicedonia', d.id FROM departments d WHERE d.name ILIKE 'Valle del Cauca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Calamar', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Caparrapí', d.id FROM departments d WHERE d.name ILIKE 'Cundinamarca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Carepa', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Cartagena De Indias', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Chigorodó', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Chinácota', d.id FROM departments d WHERE d.name ILIKE 'Norte de Santander'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Chinú', d.id FROM departments d WHERE d.name ILIKE 'Córdoba'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Circasia', d.id FROM departments d WHERE d.name ILIKE 'Quindío'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'CLEMENCIA', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Colosó', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Consacá', d.id FROM departments d WHERE d.name ILIKE 'Nariño'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Cucutilla', d.id FROM departments d WHERE d.name ILIKE 'Norte de Santander'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Curumaní', d.id FROM departments d WHERE d.name ILIKE 'Cesar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'El Carmen De Viboral', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'El Charco', d.id FROM departments d WHERE d.name ILIKE 'Nariño'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'El Litoral Del San Juan', d.id FROM departments d WHERE d.name ILIKE 'Chocó'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'El Tarra', d.id FROM departments d WHERE d.name ILIKE 'Norte de Santander'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'El Zulia', d.id FROM departments d WHERE d.name ILIKE 'Norte de Santander'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Fresno', d.id FROM departments d WHERE d.name ILIKE 'Tolima'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Galapa', d.id FROM departments d WHERE d.name ILIKE 'Atlántico'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Galeras', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Garagoa', d.id FROM departments d WHERE d.name ILIKE 'Boyacá'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Girardota', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Granada', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Guadalajara De Buga', d.id FROM departments d WHERE d.name ILIKE 'Valle del Cauca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Guamal', d.id FROM departments d WHERE d.name ILIKE 'Magdalena'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Guamo', d.id FROM departments d WHERE d.name ILIKE 'Tolima'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Guaranda', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Guática', d.id FROM departments d WHERE d.name ILIKE 'Risaralda'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'La Cruz', d.id FROM departments d WHERE d.name ILIKE 'Nariño'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'La Florida', d.id FROM departments d WHERE d.name ILIKE 'Nariño'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'La Tebaida', d.id FROM departments d WHERE d.name ILIKE 'Quindío'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'La Unión', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'La Unión', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Los Palmitos', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Mahates', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Majagual', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Mallama', d.id FROM departments d WHERE d.name ILIKE 'Nariño'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Manatí', d.id FROM departments d WHERE d.name ILIKE 'Atlántico'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Manaure Balcón Del Cesar', d.id FROM departments d WHERE d.name ILIKE 'Cesar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'María La Baja', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Momil', d.id FROM departments d WHERE d.name ILIKE 'Córdoba'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Neira', d.id FROM departments d WHERE d.name ILIKE 'Caldas'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Nuevo Colón', d.id FROM departments d WHERE d.name ILIKE 'Boyacá'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Nóvita', d.id FROM departments d WHERE d.name ILIKE 'Chocó'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Ovejas', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Pailitas', d.id FROM departments d WHERE d.name ILIKE 'Cesar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Pasca', d.id FROM departments d WHERE d.name ILIKE 'Cundinamarca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Piendamó - Tunía', d.id FROM departments d WHERE d.name ILIKE 'Cauca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Pivijay', d.id FROM departments d WHERE d.name ILIKE 'Magdalena'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Planadas', d.id FROM departments d WHERE d.name ILIKE 'Tolima'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Planeta Rica', d.id FROM departments d WHERE d.name ILIKE 'Córdoba'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Puerto Gaitán', d.id FROM departments d WHERE d.name ILIKE 'Meta'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Puerto Leguízamo', d.id FROM departments d WHERE d.name ILIKE 'Putumayo'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'PUERTO SANTANDER', d.id FROM departments d WHERE d.name ILIKE 'Norte de Santander'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Repelón', d.id FROM departments d WHERE d.name ILIKE 'Atlántico'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Restrepo', d.id FROM departments d WHERE d.name ILIKE 'Meta'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Ricaurte', d.id FROM departments d WHERE d.name ILIKE 'Cundinamarca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Risaralda', d.id FROM departments d WHERE d.name ILIKE 'Caldas'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Rosas', d.id FROM departments d WHERE d.name ILIKE 'Cauca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Rovira', d.id FROM departments d WHERE d.name ILIKE 'Tolima'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Sabanagrande', d.id FROM departments d WHERE d.name ILIKE 'Atlántico'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Sabanalarga', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Sabanalarga', d.id FROM departments d WHERE d.name ILIKE 'Casanare'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Sabaneta', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Salazar De Las Palmas', d.id FROM departments d WHERE d.name ILIKE 'Norte de Santander'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Sampués', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Andrés De Sotavento', d.id FROM departments d WHERE d.name ILIKE 'Córdoba'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Andrés De Tumaco', d.id FROM departments d WHERE d.name ILIKE 'Nariño'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Antero', d.id FROM departments d WHERE d.name ILIKE 'Córdoba'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Cayetano', d.id FROM departments d WHERE d.name ILIKE 'Norte de Santander'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Estanislao', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Jacinto', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Juan De Betulia', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Juan Nepomuceno', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Luis De Sincé', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Onofre', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Pablo', d.id FROM departments d WHERE d.name ILIKE 'Nariño'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Pablo Sur', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Pedro', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Rafael', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'San Sebastián De Buenavista', d.id FROM departments d WHERE d.name ILIKE 'Magdalena'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Santa Bárbara', d.id FROM departments d WHERE d.name ILIKE 'Nariño'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Santa Rosa De Lima', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Santiago De Tolú', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Santo Tomás', d.id FROM departments d WHERE d.name ILIKE 'Atlántico'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Sibundoy', d.id FROM departments d WHERE d.name ILIKE 'Putumayo'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Soplaviento', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Tarazá', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Timbiquí', d.id FROM departments d WHERE d.name ILIKE 'Cauca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Timbío', d.id FROM departments d WHERE d.name ILIKE 'Cauca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Tolú Viejo', d.id FROM departments d WHERE d.name ILIKE 'Sucre'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Toro', d.id FROM departments d WHERE d.name ILIKE 'Valle del Cauca'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Tuchín', d.id FROM departments d WHERE d.name ILIKE 'Córdoba'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Turbaná', d.id FROM departments d WHERE d.name ILIKE 'Bolívar'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Villanueva', d.id FROM departments d WHERE d.name ILIKE 'Santander'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Yarumal', d.id FROM departments d WHERE d.name ILIKE 'Antioquia'
ON CONFLICT (name, department_id) DO NOTHING;

INSERT INTO cities (name, department_id)
SELECT 'Zarzal', d.id FROM departments d WHERE d.name ILIKE 'Valle del Cauca'
ON CONFLICT (name, department_id) DO NOTHING;


-- 2) Re-insertar direcciones para todos los clientes (ON CONFLICT omite los que ya existen)
INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('0001', 'Florencia', 'Caquetá'),
    ('0002', 'Cali', 'Valle Del Cauca'),
    ('0003', 'Timbío', 'Cauca'),
    ('0004', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('0005', 'Popayán', 'Cauca'),
    ('0006', 'Timbío', 'Cauca'),
    ('0007', 'Ipiales', 'Nariño'),
    ('0008', 'Túquerres', 'Nariño'),
    ('0010', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('0011', 'Ibagué', 'Tolima'),
    ('0012', 'Ibagué', 'Tolima'),
    ('009', 'Caucasia', 'Antioquia'),
    ('1000440327-1', 'Cartagena De Indias', 'Bolívar'),
    ('1000468655-4', 'Soacha', 'Cundinamarca'),
    ('1000633782-9', 'San Rafael', 'Antioquia'),
    ('1001027132-6', 'Cartagena De Indias', 'Bolívar'),
    ('1001271046-5', 'Soacha', 'Cundinamarca'),
    ('1001525715-6', 'Cartagena De Indias', 'Bolívar'),
    ('1001968227-4', 'Cartagena De Indias', 'Bolívar'),
    ('1001971834-6', 'Cartagena De Indias', 'Bolívar'),
    ('1001974153-2', 'Turbaco', 'Bolívar'),
    ('1001976182-5', 'Cartagena De Indias', 'Bolívar'),
    ('1001979611-7', 'Cartagena De Indias', 'Bolívar'),
    ('1002127419-7', 'Barranquilla', 'Atlántico'),
    ('1002153059-9', 'San Jacinto', 'Bolívar'),
    ('1002196982-7', 'Cartagena De Indias', 'Bolívar'),
    ('1002202863-5', 'Cartagena De Indias', 'Bolívar'),
    ('1002241962-2', 'Turbaco', 'Bolívar'),
    ('1002320017-6', 'María La Baja', 'Bolívar'),
    ('1002411820-5', 'Soplaviento', 'Bolívar'),
    ('1002430952-1', 'Cartagena De Indias', 'Bolívar'),
    ('1002442563', 'El Carmen De Bolívar', 'Bolívar'),
    ('1002474184-9', 'Cúcuta', 'Norte De Santander'),
    ('1002474311-8', 'Cartagena De Indias', 'Bolívar'),
    ('1002475928-6', 'Cartagena De Indias', 'Bolívar'),
    ('1002594354-9', 'Riosucio', 'Caldas'),
    ('1002840514-6', 'Popayán', 'Cauca'),
    ('1002968660-4', 'Popayán', 'Cauca'),
    ('1004628390-1', 'Popayán', 'Cauca'),
    ('1004795408-8', 'Villa Del Rosario', 'Norte De Santander'),
    ('1004799586-9', 'La Tebaida', 'Quindío'),
    ('1004804040-1', 'Cúcuta', 'Norte De Santander'),
    ('1004808541-8', 'Cúcuta', 'Norte De Santander'),
    ('1004842844-8', 'Villa Del Rosario', 'Norte De Santander'),
    ('1004843388-5', 'Cúcuta', 'Norte De Santander'),
    ('1004876917-3', 'Cúcuta', 'Norte De Santander'),
    ('1004899089-9', 'Ocaña', 'Norte De Santander'),
    ('1004913608-1', 'Cúcuta', 'Norte De Santander'),
    ('1004960271-3', 'Armenia', 'Quindío'),
    ('1004967096-2', 'Popayán', 'Cauca')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('1005093601-4', 'Armenia', 'Quindío'),
    ('1005236085-9', 'Cúcuta', 'Norte De Santander'),
    ('1005469201', 'Guaranda', 'Sucre'),
    ('1005568844-7', 'Sincelejo', 'Sucre'),
    ('1005603517-3', 'Sincelejo', 'Sucre'),
    ('1005627346-4', 'Sincelejo', 'Sucre'),
    ('1005663880-9', 'Sincelejo', 'Sucre'),
    ('1005714913', 'Ibagué', 'Tolima'),
    ('1005834044-2', 'Armenia', 'Quindío'),
    ('1006857587-2', 'San José Del Guaviare', 'Guaviare'),
    ('1006860422-7', 'La Tebaida', 'Quindío'),
    ('1006908165-8', 'Sibundoy', 'Putumayo'),
    ('1006994598-1', 'Ricaurte', 'Cundinamarca'),
    ('1006995321-1', 'Magangué', 'Bolívar'),
    ('1007189810-8', 'Cartagena De Indias', 'Bolívar'),
    ('1007195409-1', 'Luruaco', 'Atlántico'),
    ('1007198003-9', 'Toro', 'Valle Del Cauca'),
    ('1007317280-4', 'Aguachica', 'Cesar'),
    ('1007345221-9', 'Pitalito', 'Huila'),
    ('1007391800-9', 'Ocaña', 'Norte De Santander'),
    ('1007767309-9', 'Sincelejo', 'Sucre'),
    ('1007974691-5', 'Cartagena De Indias', 'Bolívar'),
    ('1007978301-6', 'Cartagena De Indias', 'Bolívar'),
    ('1010062150-1', 'Maicao', 'La Guajira'),
    ('1010115393-1', 'Cartagena De Indias', 'Bolívar'),
    ('1010137601-3', 'Villa Del Rosario', 'Norte De Santander'),
    ('1012373755-7', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('1012405152-5', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('1013642955-1', 'Fresno', 'Tolima'),
    ('10140047-2', 'Pereira', 'Risaralda'),
    ('1014197174', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('1017167335-2', 'Medellín', 'Antioquia'),
    ('1018422463-4', 'La Tebaida', 'Quindío'),
    ('1018504571-4', 'Cartagena De Indias', 'Bolívar'),
    ('1019102386-6', 'Cartagena De Indias', 'Bolívar'),
    ('1020458784-6', 'San Antero', 'Córdoba'),
    ('1020724264-9', 'Sincelejo', 'Sucre'),
    ('1020764544-7', 'Ricaurte', 'Cundinamarca'),
    ('1020791078-0', 'Villa Del Rosario', 'Norte De Santander'),
    ('1022328845-7', 'San Benito Abad', 'Sucre'),
    ('1023723199', 'Tadó', 'Chocó'),
    ('10250475-3', 'Armenia', 'Quindío'),
    ('10274154-8', 'Manizales', 'Caldas'),
    ('10283826-7', 'Manizales', 'Caldas'),
    ('1030529095-1', 'Inírida', 'Guainía'),
    ('1030545066-5', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('10307399-9', 'Pasto', 'Nariño'),
    ('1031141209-0', 'Santander De Quilichao', 'Cauca'),
    ('1032357931', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('1032370318-0', 'Bogotá, D.C.', 'Bogotá D.C.')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('1033713396-2', 'San Onofre', 'Sucre'),
    ('1035914486-7', 'Cartagena De Indias', 'Bolívar'),
    ('1036394233-1', 'Caparrapí', 'Cundinamarca'),
    ('1036599597-8', 'El Carmen De Bolívar', 'Bolívar'),
    ('1037603509-0', 'Tarazá', 'Antioquia'),
    ('1037636646-3', 'Armenia', 'Quindío'),
    ('1037946238-1', 'Cartagena De Indias', 'Bolívar'),
    ('1038821532-9', 'Chigorodó', 'Antioquia'),
    ('1039596469', 'El Charco', 'Nariño'),
    ('1039622127-9', 'Buenaventura', 'Valle Del Cauca'),
    ('1040261000-6', 'Turbaná', 'Bolívar'),
    ('1043639851-2', 'Cartagena De Indias', 'Bolívar'),
    ('1043646678-3', 'Cartagena De Indias', 'Bolívar'),
    ('1043964884-8', 'Cartagena De Indias', 'Bolívar'),
    ('1043968025-6', 'Cartagena De Indias', 'Bolívar'),
    ('1044101576-5', 'Cartagena De Indias', 'Bolívar'),
    ('1044913823-4', 'Arjona', 'Bolívar'),
    ('1044919215-3', 'Arjona', 'Bolívar'),
    ('1044928834-0', 'Cartagena De Indias', 'Bolívar'),
    ('1044932359-9', 'Arjona', 'Bolívar'),
    ('1045018782-5', 'San Onofre', 'Sucre'),
    ('1045023891-1', 'San Onofre', 'Sucre'),
    ('1045023959-1', 'Tuchín', 'Córdoba'),
    ('1045024914', 'Sampués', 'Sucre'),
    ('1045025467-9', 'Cartagena De Indias', 'Bolívar'),
    ('1045076585-8', 'El Carmen De Viboral', 'Antioquia'),
    ('1045669014-1', 'Aguachica', 'Cesar'),
    ('1045728385-0', 'Arjona', 'Bolívar'),
    ('1046402170-8', 'Achí', 'Bolívar'),
    ('1046667540-7', 'Cartagena De Indias', 'Bolívar'),
    ('1047224372-9', 'Luruaco', 'Atlántico'),
    ('1047379726-8', 'Cartagena De Indias', 'Bolívar'),
    ('1047389699-1', 'Cartagena De Indias', 'Bolívar'),
    ('1047396611-1', 'Cartagena De Indias', 'Bolívar'),
    ('1047416787-6', 'Cartagena De Indias', 'Bolívar'),
    ('1047422286-2', 'Cartagena De Indias', 'Bolívar'),
    ('1047424357-6', 'Cartagena De Indias', 'Bolívar'),
    ('1047431705-5', 'Cartagena De Indias', 'Bolívar'),
    ('1047438704-1', 'Cartagena De Indias', 'Bolívar'),
    ('1047438788-8', 'Cartagena De Indias', 'Bolívar'),
    ('1047451062-3', 'Cartagena De Indias', 'Bolívar'),
    ('1047461048-2', 'Cartagena De Indias', 'Bolívar'),
    ('1047463013-4', 'Cartagena De Indias', 'Bolívar'),
    ('1047468932-0', 'Cartagena De Indias', 'Bolívar'),
    ('1047470456-2', 'Cartagena De Indias', 'Bolívar'),
    ('1047471787-1', 'Cartagena De Indias', 'Bolívar'),
    ('1047475741-1', 'Cartagena De Indias', 'Bolívar'),
    ('1047485259-3', 'Mahates', 'Bolívar'),
    ('1047512080-9', 'Cartagena De Indias', 'Bolívar'),
    ('1048434532-5', 'Cartagena De Indias', 'Bolívar')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('1048435654-1', 'Cartagena De Indias', 'Bolívar'),
    ('1048436165-4', 'Cartagena Del Chairá', 'Caquetá'),
    ('1048457318-4', 'Santa Rosa De Cabal', 'Risaralda'),
    ('1048608457-9', 'Cartagena De Indias', 'Bolívar'),
    ('1048847555-7', 'Garagoa', 'Boyacá'),
    ('1048938398-8', 'Cartagena De Indias', 'Bolívar'),
    ('1049396357-2', 'Cúcuta', 'Norte De Santander'),
    ('1049534676-1', 'Cartagena De Indias', 'Bolívar'),
    ('1049537316-7', 'Cartagena De Indias', 'Bolívar'),
    ('1049539229-3', 'San Estanislao', 'Bolívar'),
    ('1049828406-0', 'CLEMENCIA', 'Bolívar'),
    ('1049932844-8', 'María La Baja', 'Bolívar'),
    ('1049939785-3', 'Cartagena De Indias', 'Bolívar'),
    ('1049946263-1', 'Cartagena De Indias', 'Bolívar'),
    ('1050039706-3', 'San Jacinto', 'Bolívar'),
    ('1050064091-8', 'Cartagena De Indias', 'Bolívar'),
    ('1050456030-1', 'Cartagena De Indias', 'Bolívar'),
    ('1050945149-7', 'Turbaco', 'Bolívar'),
    ('1050946953-7', 'Turbaco', 'Bolívar'),
    ('1050954267-6', 'Turbaco', 'Bolívar'),
    ('1050968075-1', 'Cartagena De Indias', 'Bolívar'),
    ('1051416119-0', 'Cartagena De Indias', 'Bolívar'),
    ('1051443463-4', 'Cartagena De Indias', 'Bolívar'),
    ('1051445987-0', 'Cartagena De Indias', 'Bolívar'),
    ('1051815841-2', 'San Juan Nepomuceno', 'Bolívar'),
    ('1051817776-0', 'Istmina', 'Chocó'),
    ('1051818097-2', 'San Juan Nepomuceno', 'Bolívar'),
    ('1051829905-6', 'San Juan Nepomuceno', 'Bolívar'),
    ('1051884508-9', 'CLEMENCIA', 'Bolívar'),
    ('1051885338-8', 'Cartagena De Indias', 'Bolívar'),
    ('1051889186-3', 'Cartagena De Indias', 'Bolívar'),
    ('1051890713-7', 'La Tebaida', 'Quindío'),
    ('1052070088-7', 'Cartagena De Indias', 'Bolívar'),
    ('1052087343-5', 'El Carmen De Bolívar', 'Bolívar'),
    ('1052090277-8', 'El Carmen De Bolívar', 'Bolívar'),
    ('1052392914-8', 'Duitama', 'Boyacá'),
    ('1052730663-5', 'Cartagena De Indias', 'Bolívar'),
    ('1053776357', 'Cali', 'Valle Del Cauca'),
    ('1053803096-7', 'Cartagena De Indias', 'Bolívar'),
    ('1053829597-8', 'Manizales', 'Caldas'),
    ('1058970023-2', 'Popayán', 'Cauca'),
    ('1061600608-5', 'Popayán', 'Cauca'),
    ('1061687549-2', 'Ipiales', 'Nariño'),
    ('1061697890-2', 'Popayán', 'Cauca'),
    ('1061726829-8', 'Popayán', 'Cauca'),
    ('1061739555-1', 'Piendamó - Tunía', 'Cauca'),
    ('1061790143-6', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('1061799725-3', 'Popayán', 'Cauca'),
    ('1061799735-7', 'Pasto', 'Nariño'),
    ('1061821764-4', 'Popayán', 'Cauca')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('1062299790-0', 'Santander De Quilichao', 'Cauca'),
    ('1063075277-5', 'Montería', 'Córdoba'),
    ('1063080129-3', 'San Andrés De Sotavento', 'Córdoba'),
    ('1063082443-0', 'San Andrés De Sotavento', 'Córdoba'),
    ('1063817127-4', 'Popayán', 'Cauca'),
    ('1063950293-7', 'Bosconia', 'Cesar'),
    ('1063954576', 'Ariguaní', 'Magdalena'),
    ('1063962416-8', 'Bosconia', 'Cesar'),
    ('1064840971-4', 'Ocaña', 'Norte De Santander'),
    ('1065203764-8', 'Becerril', 'Cesar'),
    ('1065638282', 'Plato', 'Magdalena'),
    ('1065875434-5', 'Bosconia', 'Cesar'),
    ('1066181409-5', 'San Andrés De Sotavento', 'Córdoba'),
    ('1066189692-1', 'Sincelejo', 'Sucre'),
    ('1066737850-9', 'Cúcuta', 'Norte De Santander'),
    ('1067036733-0', 'Riohacha', 'La Guajira'),
    ('1067401854-9', 'San Andrés De Sotavento', 'Córdoba'),
    ('1067853098-6', 'Sincelejo', 'Sucre'),
    ('1067865459-3', 'Santiago De Tolú', 'Sucre'),
    ('1067931149-8', 'Medellín', 'Antioquia'),
    ('1069494333-9', 'Galeras', 'Sucre'),
    ('1069986434-6', 'San Andrés De Sotavento', 'Córdoba'),
    ('1071986520-9', 'Soacha', 'Cundinamarca'),
    ('1072703113-4', 'Barrancabermeja', 'Santander'),
    ('1073674960-4', 'Pitalito', 'Huila'),
    ('1075278920-9', 'Ibagué', 'Tolima'),
    ('10755361-0', 'Buenaventura', 'Valle Del Cauca'),
    ('1075627753-3', 'San Andrés De Sotavento', 'Córdoba'),
    ('1076329234', 'Istmina', 'Chocó'),
    ('1077457401-3', 'Armenia', 'Quindío'),
    ('1077462911-8', 'Cartagena De Indias', 'Bolívar'),
    ('1079173317-1', 'La Plata', 'Huila'),
    ('1081907592-8', 'Plato', 'Magdalena'),
    ('1081913102-7', 'Plato', 'Magdalena'),
    ('1081914096', 'Plato', 'Magdalena'),
    ('1081926883-7', 'Plato', 'Magdalena'),
    ('1082491918-0', 'Sabanalarga', 'Antioquia'),
    ('1082930657-8', 'Santa Marta', 'Magdalena'),
    ('1085036486-1', 'El Banco', 'Magdalena'),
    ('1085263486-4', 'Túquerres', 'Nariño'),
    ('1085276355-4', 'Pasto', 'Nariño'),
    ('1085289648-3', 'Pasto', 'Nariño'),
    ('1085298432-8', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('1085319751-4', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('1085322422-7', 'Pasto', 'Nariño'),
    ('1085904292-4', 'Pasto', 'Nariño'),
    ('1085906585-6', 'Ipiales', 'Nariño'),
    ('1085952060-7', 'Ipiales', 'Nariño'),
    ('1087047100-9', 'Pasto', 'Nariño'),
    ('1087112786-9', 'San Andrés De Tumaco', 'Nariño')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('1087114376-1', 'San Andrés De Tumaco', 'Nariño'),
    ('1087406456-6', 'Túquerres', 'Nariño'),
    ('1087418477-2', 'Túquerres', 'Nariño'),
    ('1087488213-4', 'Dosquebradas', 'Risaralda'),
    ('10877735-5', 'San Marcos', 'Sucre'),
    ('1088006078', 'Risaralda', 'Caldas'),
    ('1088300550-8', 'Pereira', 'Risaralda'),
    ('1088317635-1', 'Pereira', 'Risaralda'),
    ('1088335635-6', 'Pereira', 'Risaralda'),
    ('10885268-0', 'San Marcos', 'Sucre'),
    ('1088944686-4', 'Popayán', 'Cauca'),
    ('1090178823-8', 'Cúcuta', 'Norte De Santander'),
    ('1090362368-6', 'Cúcuta', 'Norte De Santander'),
    ('1090367256-2', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('1090369834-9', 'Cúcuta', 'Norte De Santander'),
    ('1090372347-4', 'Villa Del Rosario', 'Norte De Santander'),
    ('1090372994-1', 'Cúcuta', 'Norte De Santander'),
    ('1090384637-7', 'Villa Del Rosario', 'Norte De Santander'),
    ('1090391586-9', 'Cúcuta', 'Norte De Santander'),
    ('1090392317-9', 'Cúcuta', 'Norte De Santander'),
    ('1090392405-9', 'Cúcuta', 'Norte De Santander'),
    ('1090392860-7', 'Villa Del Rosario', 'Norte De Santander'),
    ('1090393625', 'Cúcuta', 'Norte De Santander'),
    ('1090397188-8', 'Cúcuta', 'Norte De Santander'),
    ('1090398549-8', 'Cúcuta', 'Norte De Santander'),
    ('1090404681-9', 'Villa Del Rosario', 'Norte De Santander'),
    ('1090425457', 'Cúcuta', 'Norte De Santander'),
    ('1090426686-1', 'Cúcuta', 'Norte De Santander'),
    ('1090427192-8', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('1090443161-7', 'Cúcuta', 'Norte De Santander'),
    ('1090447747-0', 'Villa Del Rosario', 'Norte De Santander'),
    ('1090465080-3', 'Villa Del Rosario', 'Norte De Santander'),
    ('1090466392-0', 'Cúcuta', 'Norte De Santander'),
    ('1090485162-4', 'Caicedo', 'Antioquia'),
    ('1090491898-0', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('1090493488-3', 'Cúcuta', 'Norte De Santander'),
    ('1090502472-6', 'Arauquita', 'Arauca'),
    ('1091363369-1', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('1091387097-7', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('1091666259-0', 'La Florida', 'Nariño'),
    ('1091671972-4', 'Ocaña', 'Norte De Santander'),
    ('1091682086-0', 'Cúcuta', 'Norte De Santander'),
    ('1091683927-4', 'Cúcuta', 'Norte De Santander'),
    ('1091964972-2', 'Cúcuta', 'Norte De Santander'),
    ('1092176907', 'Ocaña', 'Norte De Santander'),
    ('1092335405-3', 'Villa Del Rosario', 'Norte De Santander'),
    ('1092341286-8', 'Villa Del Rosario', 'Norte De Santander'),
    ('1092342307-9', 'Cúcuta', 'Norte De Santander'),
    ('1092345767-7', 'Villa Del Rosario', 'Norte De Santander'),
    ('1092350821-7', 'PUERTO SANTANDER', 'Norte De Santander')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('1092357534-1', 'Cúcuta', 'Norte De Santander'),
    ('1093739134', 'Cúcuta', 'Norte De Santander'),
    ('1093741646-6', 'Villa Del Rosario', 'Norte De Santander'),
    ('1093745676-5', 'Los Patios', 'Norte De Santander'),
    ('1093749859-4', 'Cúcuta', 'Norte De Santander'),
    ('1093750495-9', 'Los Patios', 'Norte De Santander'),
    ('1093761748-4', 'Villa Del Rosario', 'Norte De Santander'),
    ('1093765234-9', 'Villa Del Rosario', 'Norte De Santander'),
    ('1093785911-2', 'San José Del Guaviare', 'Guaviare'),
    ('1093790343-9', 'Villa Del Rosario', 'Norte De Santander'),
    ('1094247661-2', 'Villa Del Rosario', 'Norte De Santander'),
    ('1094287198', 'Villa Del Rosario', 'Norte De Santander'),
    ('1094350060', 'Cúcuta', 'Norte De Santander'),
    ('1094882361-0', 'Armenia', 'Quindío'),
    ('1094886296-8', 'La Tebaida', 'Quindío'),
    ('1094887468-2', 'Buenaventura', 'Valle Del Cauca'),
    ('1094888392-6', 'Armenia', 'Quindío'),
    ('1094903006-2', 'Armenia', 'Quindío'),
    ('1094919749-6', 'Armenia', 'Quindío'),
    ('1094929675-2', 'Armenia', 'Quindío'),
    ('1094933523-7', 'Armenia', 'Quindío'),
    ('1094935802-6', 'Armenia', 'Quindío'),
    ('1094953375-9', 'Armenia', 'Quindío'),
    ('1094971400-1', 'Armenia', 'Quindío'),
    ('1094972223-9', 'Armenia', 'Quindío'),
    ('1094974711-0', 'Armenia', 'Quindío'),
    ('1096035940-1', 'Calarcá', 'Quindío'),
    ('1096036433-1', 'La Tebaida', 'Quindío'),
    ('1096041038-5', 'La Tebaida', 'Quindío'),
    ('1097039680-1', 'Quimbaya', 'Quindío'),
    ('1097724135-7', 'Montenegro', 'Quindío'),
    ('1098606001-7', 'Piedecuesta', 'Santander'),
    ('1098615044-1', 'Cúcuta', 'Norte De Santander'),
    ('1098701707-4', 'Fundación', 'Magdalena'),
    ('1098751507-1', 'Cartagena De Indias', 'Bolívar'),
    ('1098830716-3', 'Bucaramanga', 'Santander'),
    ('1099209502-6', 'Barbosa', 'Santander'),
    ('11000737-1', 'San Marcos', 'Sucre'),
    ('1100392271-4', 'Sincelejo', 'Sucre'),
    ('1100394800-1', 'Sincelejo', 'Sucre'),
    ('1100543103-4', 'Galeras', 'Sucre'),
    ('1100544454-9', 'Galeras', 'Sucre'),
    ('1100548786-7', 'Galeras', 'Sucre'),
    ('1100625175-7', 'Sincelejo', 'Sucre'),
    ('1101388331-6', 'Guaranda', 'Sucre'),
    ('1101388460-8', 'Guaranda', 'Sucre'),
    ('1101389999-1', 'Guaranda', 'Sucre'),
    ('1101391930-9', 'Guaranda', 'Sucre'),
    ('1101448540-7', 'San Onofre', 'Sucre'),
    ('1101451552-6', 'San Onofre', 'Sucre')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('1101457364-5', 'San Onofre', 'Sucre'),
    ('1101693611-0', 'Barbosa', 'Santander'),
    ('1101696112-0', 'San Onofre', 'Sucre'),
    ('1101815988-7', 'Ovejas', 'Sucre'),
    ('1101820152', 'Sampués', 'Sucre'),
    ('1102122884-9', 'La Unión', 'Sucre'),
    ('1102232978-4', 'San Benito Abad', 'Sucre'),
    ('1102580054-6', 'Galeras', 'Sucre'),
    ('1102794225-8', 'Sincelejo', 'Sucre'),
    ('1102803959-5', 'Sincelejo', 'Sucre'),
    ('1102807725-7', 'Sincelejo', 'Sucre'),
    ('1102810689-0', 'Sincelejo', 'Sucre'),
    ('1102810938-1', 'Cartagena De Indias', 'Bolívar'),
    ('1102812232-8', 'San Marcos', 'Sucre'),
    ('1102817799-4', 'Sincelejo', 'Sucre'),
    ('1102831693-0', 'Corozal', 'Sucre'),
    ('1102833501-4', 'Sincelejo', 'Sucre'),
    ('1102835607-5', 'Sincelejo', 'Sucre'),
    ('1102838167-1', 'Sincelejo', 'Sucre'),
    ('1102847080-6', 'Sincelejo', 'Sucre'),
    ('1102848516-1', 'Sincelejo', 'Sucre'),
    ('1102853057-0', 'Sincelejo', 'Sucre'),
    ('1102857593-5', 'Sincelejo', 'Sucre'),
    ('1102857727-5', 'Sincelejo', 'Sucre'),
    ('1102861551-1', 'Sincelejo', 'Sucre'),
    ('1102867832-3', 'Sampués', 'Sucre'),
    ('1102873981-7', 'Sincelejo', 'Sucre'),
    ('1102875613-0', 'Sincelejo', 'Sucre'),
    ('1102876479-4', 'Sincelejo', 'Sucre'),
    ('1102889207-4', 'Sincelejo', 'Sucre'),
    ('1103112420-6', 'Corozal', 'Sucre'),
    ('1103117376-2', 'Cartagena De Indias', 'Bolívar'),
    ('11037195-8', 'San Andrés De Sotavento', 'Córdoba'),
    ('1103948371-1', 'San Juan De Betulia', 'Sucre'),
    ('1104378326-7', 'Manatí', 'Atlántico'),
    ('1104378583-3', 'Majagual', 'Sucre'),
    ('1104382311-2', 'Majagual', 'Sucre'),
    ('1104711408-0', 'Líbano', 'Tolima'),
    ('1104869315-3', 'Santiago De Tolú', 'Sucre'),
    ('11051122-9', 'Sincelejo', 'Sucre'),
    ('11063582-5', 'San Andrés De Sotavento', 'Córdoba'),
    ('1106516344-1', 'Popayán', 'Cauca'),
    ('1107076818-3', 'Buenaventura', 'Valle Del Cauca'),
    ('1108764025-5', 'San Andrés De Sotavento', 'Córdoba'),
    ('1109114158-1', 'Armenia', 'Quindío'),
    ('1110470999-8', 'Guamo', 'Tolima'),
    ('1110491466-4', 'Ibagué', 'Tolima'),
    ('1110528955-6', 'Ibagué', 'Tolima'),
    ('1110529993', 'Ibagué', 'Tolima'),
    ('1110551416-4', 'Ibagué', 'Tolima')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('1114388817-0', 'Restrepo', 'Meta'),
    ('1115184608-5', 'Cúcuta', 'Norte De Santander'),
    ('1115422653-9', 'Toro', 'Valle Del Cauca'),
    ('1115727506-4', 'Saravena', 'Arauca'),
    ('1116232354', 'Tuluá', 'Valle Del Cauca'),
    ('1116233254-6', 'Tuluá', 'Valle Del Cauca'),
    ('1116261137', 'Tuluá', 'Valle Del Cauca'),
    ('1116442612', 'Zarzal', 'Valle Del Cauca'),
    ('1116791220-9', 'Cúcuta', 'Norte De Santander'),
    ('1116820242-6', 'Cartagena De Indias', 'Bolívar'),
    ('1117488212-5', 'Florencia', 'Caquetá'),
    ('1118815143-2', 'Riohacha', 'La Guajira'),
    ('1120744695-8', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('11228380-4', 'Espinal', 'Tolima'),
    ('1124514615-7', 'Manaure Balcón Del Cesar', 'Cesar'),
    ('1125759332-3', 'Arauca', 'Arauca'),
    ('1127360790-6', 'Cúcuta', 'Norte De Santander'),
    ('1127581991-8', 'San Estanislao', 'Bolívar'),
    ('1127593467-1', 'Cucutilla', 'Norte De Santander'),
    ('1127611905-4', 'Cartagena De Indias', 'Bolívar'),
    ('1128048970-2', 'Cartagena De Indias', 'Bolívar'),
    ('1128052954-1', 'Cartagena De Indias', 'Bolívar'),
    ('1128055020-1', 'Cartagena De Indias', 'Bolívar'),
    ('1128060472-5', 'Cartagena De Indias', 'Bolívar'),
    ('1129495624-9', 'Galeras', 'Sucre'),
    ('1129515775-1', 'Chía', 'Cundinamarca'),
    ('1130633533-3', 'Tuluá', 'Valle Del Cauca'),
    ('1131108202-3', 'Santiago De Tolú', 'Sucre'),
    ('1133844041-8', 'San Andrés De Sotavento', 'Córdoba'),
    ('1143325745-4', 'El Litoral Del San Juan', 'Chocó'),
    ('1143334167-5', 'Cartagena De Indias', 'Bolívar'),
    ('1143343714-2', 'Cartagena De Indias', 'Bolívar'),
    ('1143345619-1', 'Cartagena De Indias', 'Bolívar'),
    ('1143361697', 'Cartagena De Indias', 'Bolívar'),
    ('1143362694-4', 'Cartagena De Indias', 'Bolívar'),
    ('1143362719-1', 'Cartagena De Indias', 'Bolívar'),
    ('1143365184-3', 'Cartagena De Indias', 'Bolívar'),
    ('1143372176-3', 'Cartagena De Indias', 'Bolívar'),
    ('1143372841-3', 'Cartagena De Indias', 'Bolívar'),
    ('1143376916-5', 'Mahates', 'Bolívar'),
    ('1143377951-8', 'Cartagena De Indias', 'Bolívar'),
    ('1143382319-2', 'Cartagena De Indias', 'Bolívar'),
    ('1143414075-1', 'Cartagena De Indias', 'Bolívar'),
    ('1144041992-0', 'Cali', 'Valle Del Cauca'),
    ('1144138957-0', 'Cali', 'Valle Del Cauca'),
    ('1148217392-3', 'Los Patios', 'Norte De Santander'),
    ('1148693340', 'Cartagena De Indias', 'Bolívar'),
    ('1148708175-9', 'Cúcuta', 'Norte De Santander'),
    ('1149461792-4', 'Villa Del Rosario', 'Norte De Santander'),
    ('1149462471-1', 'Villa Del Rosario', 'Norte De Santander')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('1152188403-5', 'Chinú', 'Córdoba'),
    ('1192748556-2', 'Cartagena De Indias', 'Bolívar'),
    ('1192763717-4', 'Becerril', 'Cesar'),
    ('1193045551-2', 'San Andrés De Sotavento', 'Córdoba'),
    ('1193083269-1', 'Turbaco', 'Bolívar'),
    ('1193236192-1', 'Malambo', 'Atlántico'),
    ('1193239219-5', 'Dosquebradas', 'Risaralda'),
    ('1193331137-2', 'Cartagena De Indias', 'Bolívar'),
    ('1193373923-5', 'Villa Del Rosario', 'Norte De Santander'),
    ('1193531272-7', 'El Carmen De Bolívar', 'Bolívar'),
    ('1193556049-9', 'Plato', 'Magdalena'),
    ('1196966946-0', 'San Andrés De Sotavento', 'Córdoba'),
    ('1201228448-6', 'Cartagena De Indias', 'Bolívar'),
    ('12135334-9', 'Cúcuta', 'Norte De Santander'),
    ('12239925-9', 'Pitalito', 'Huila'),
    ('12274173-5', 'La Plata', 'Huila'),
    ('1233192208-4', 'Pasto', 'Nariño'),
    ('1243139224-4', 'Villa Del Rosario', 'Norte De Santander'),
    ('1243140967-1', 'Arauca', 'Arauca'),
    ('1245278399-7', 'Cúcuta', 'Norte De Santander'),
    ('12600971-5', 'Cartagena De Indias', 'Bolívar'),
    ('12685169-9', 'Bosconia', 'Cesar'),
    ('12909549-8', 'Armenia', 'Quindío'),
    ('12962077-8', 'Pasto', 'Nariño'),
    ('12976171-3', 'Pasto', 'Nariño'),
    ('130278792', 'Republica Dominicana', 'República Dominicana'),
    ('13039837', 'Republica Dominicana', 'República Dominicana'),
    ('13053632-1', 'San Andrés De Tumaco', 'Nariño'),
    ('13167526-7', 'Sincelejo', 'Sucre'),
    ('13196446-1', 'Cúcuta', 'Norte De Santander'),
    ('13236979-6', 'Villa Del Rosario', 'Norte De Santander'),
    ('13305502-3', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('13362950-2', 'Bosconia', 'Cesar'),
    ('13452445-0', 'Cúcuta', 'Norte De Santander'),
    ('13470038-2', 'Cúcuta', 'Norte De Santander'),
    ('13477484-6', 'Cúcuta', 'Norte De Santander'),
    ('13480834-1', 'Cúcuta', 'Norte De Santander'),
    ('13493864-9', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('13496846-1', 'Cúcuta', 'Norte De Santander'),
    ('13620560', 'Malambo', 'Atlántico'),
    ('14192916', 'Planadas', 'Tolima'),
    ('14212809-2', 'Ibagué', 'Tolima'),
    ('14240090-3', 'Ibagué', 'Tolima'),
    ('14476011-5', 'El Charco', 'Nariño'),
    ('14566214-1', 'Cartago', 'Valle Del Cauca'),
    ('14896043-2', 'Popayán', 'Cauca'),
    ('14899974-8', 'Guadalajara De Buga', 'Valle Del Cauca'),
    ('15076068-1', 'Cartagena De Indias', 'Bolívar'),
    ('15207632-1', 'Maicao', 'La Guajira'),
    ('15435934-6', 'Cartagena De Indias', 'Bolívar')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('155753078', 'Panamá', 'Panamá'),
    ('15619688-1', 'San Antero', 'Córdoba'),
    ('15678178-7', 'Planeta Rica', 'Córdoba'),
    ('15700724-2', 'Momil', 'Córdoba'),
    ('15725802-7', 'San Andrés De Sotavento', 'Córdoba'),
    ('15921538-7', 'Manizales', 'Caldas'),
    ('15931890-8', 'Cartagena De Indias', 'Bolívar'),
    ('15987957-3', 'Valledupar', 'Cesar'),
    ('16076704-1', 'Manizales', 'Caldas'),
    ('16480738-3', 'Florencia', 'Caquetá'),
    ('16712674-8', 'Pereira', 'Risaralda'),
    ('16884849-7', 'Buenaventura', 'Valle Del Cauca'),
    ('16894972-8', 'Timbío', 'Cauca'),
    ('17337365-2', 'Leticia', 'Amazonas'),
    ('17689753-7', 'Santa Bárbara', 'Nariño'),
    ('18394245-3', 'Santa Marta', 'Magdalena'),
    ('18396052-8', 'Calarcá', 'Quindío'),
    ('18419787-3', 'Armenia', 'Quindío'),
    ('18465737-0', 'Quimbaya', 'Quindío'),
    ('18467585-7', 'Quimbaya', 'Quindío'),
    ('18495859-9', 'Armenia', 'Quindío'),
    ('18512660-4', 'Ibagué', 'Tolima'),
    ('18611458-7', 'Dosquebradas', 'Risaralda'),
    ('18777450-1', 'Corozal', 'Sucre'),
    ('18778387-1', 'Santa Bárbara', 'Nariño'),
    ('18882688-6', 'Ovejas', 'Sucre'),
    ('18969882-4', 'Curumaní', 'Cesar'),
    ('19345153-9', 'Bosconia', 'Cesar'),
    ('19361502-3', 'Madrid', 'Cundinamarca'),
    ('19707826-1', 'Bosconia', 'Cesar'),
    ('19897824-0', 'Cartagena De Indias', 'Bolívar'),
    ('20611872063', 'Perú', 'Perú'),
    ('20613368401', 'Lima', 'Perú'),
    ('20957775-1', 'Espinal', 'Tolima'),
    ('22081969-8', 'Cartagena De Indias', 'Bolívar'),
    ('22598326-2', 'Cartagena De Indias', 'Bolívar'),
    ('22634135-7', 'Sabanalarga', 'Casanare'),
    ('22790543-6', 'Cartagena De Indias', 'Bolívar'),
    ('22805850-1', 'Cartagena De Indias', 'Bolívar'),
    ('22807838-1', 'Cartagena De Indias', 'Bolívar'),
    ('22810597-0', 'Cartagena De Indias', 'Bolívar'),
    ('22949961-7', 'Cartagena De Indias', 'Bolívar'),
    ('23181757-6', 'Sincelejo', 'Sucre'),
    ('23182901-5', 'Sincelejo', 'Sucre'),
    ('23183763-1', 'San Luis De Sincé', 'Sucre'),
    ('23215429-3', 'Santiago De Tolú', 'Sucre'),
    ('23221625-5', 'Cartagena De Indias', 'Bolívar'),
    ('24385949-3', 'Anserma', 'Caldas'),
    ('24498158-1', 'Cali', 'Valle Del Cauca'),
    ('24688506-5', 'Guática', 'Risaralda')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('24853826-4', 'Chinchiná', 'Caldas'),
    ('24867198-8', 'Tuluá', 'Valle Del Cauca'),
    ('24989904-5', 'Armenia', 'Quindío'),
    ('25018412-1', 'Quimbaya', 'Quindío'),
    ('25019436-2', 'Quimbaya', 'Quindío'),
    ('25022152-7', 'Quimbaya', 'Quindío'),
    ('25024849-0', 'Quimbaya', 'Quindío'),
    ('25214420-0', 'Cúcuta', 'Norte De Santander'),
    ('25274170-0', 'Girardot', 'Cundinamarca'),
    ('25279218-8', 'Timbiquí', 'Cauca'),
    ('25786973-5', 'Montería', 'Córdoba'),
    ('25909722-3', 'Sincelejo', 'Sucre'),
    ('25910485-4', 'Chinú', 'Córdoba'),
    ('26719747-5', 'Ciénaga De Oro', 'Córdoba'),
    ('27080698-0', 'Ricaurte', 'Cundinamarca'),
    ('27250962-0', 'Mallama', 'Nariño'),
    ('27254148-1', 'Ipiales', 'Nariño'),
    ('27452403-2', 'La Cruz', 'Nariño'),
    ('27600098-4', 'Cúcuta', 'Norte De Santander'),
    ('27604727-7', 'Cúcuta', 'Norte De Santander'),
    ('27720042-7', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('27741977-8', 'Ocaña', 'Norte De Santander'),
    ('27891524-8', 'Cúcuta', 'Norte De Santander'),
    ('27891724', 'Cúcuta', 'Norte De Santander'),
    ('27895670-3', 'Los Patios', 'Norte De Santander'),
    ('27898630-2', 'Cartagena De Indias', 'Bolívar'),
    ('28023590-5', 'Turbaco', 'Bolívar'),
    ('28023663-4', 'Arjona', 'Bolívar'),
    ('28548362-6', 'Piendamó - Tunía', 'Cauca'),
    ('2907926-6', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('29185764-8', 'Popayán', 'Cauca'),
    ('29361495-6', 'Armenia', 'Quindío'),
    ('29765561', 'Cartagena De Indias', 'Bolívar'),
    ('29993411-1', 'Zarzal', 'Valle Del Cauca'),
    ('301128569', 'Estados Unidos', 'Estados Unidos'),
    ('30230403-0', 'Manizales', 'Caldas'),
    ('30335343-9', 'Armenia', 'Quindío'),
    ('303429181', 'Caracas', 'Venezuela'),
    ('30382992-9', 'Riosucio', 'Caldas'),
    ('30666681-3', 'Santiago De Tolú', 'Sucre'),
    ('30714390-1', 'Pasto', 'Nariño'),
    ('30763428-1', 'Arjona', 'Bolívar'),
    ('30766311-2', 'Arjona', 'Bolívar'),
    ('30767988-2', 'Arjona', 'Bolívar'),
    ('30844215', 'Calamar', 'Bolívar'),
    ('30854798-2', 'Mahates', 'Bolívar'),
    ('30878439-7', 'Turbaco', 'Bolívar'),
    ('30882889-3', 'Arjona', 'Bolívar'),
    ('30898367-0', 'San Juan Nepomuceno', 'Bolívar'),
    ('30898678-6', 'Sabanalarga', 'Atlántico')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('31794492-1', 'Zarzal', 'Valle Del Cauca'),
    ('32142338-1', 'Medellín', 'Antioquia'),
    ('32277543-3', 'Caucasia', 'Antioquia'),
    ('32294159-1', 'Chigorodó', 'Antioquia'),
    ('3232807-3', 'Manizales', 'Caldas'),
    ('32391109-7', 'Cartagena De Indias', 'Bolívar'),
    ('32391661-1', 'Cartagena De Indias', 'Bolívar'),
    ('32393636-6', 'Cartagena De Indias', 'Bolívar'),
    ('32660670-2', 'Sincelejo', 'Sucre'),
    ('328100-8', 'Cúcuta', 'Norte De Santander'),
    ('32812463-8', 'Soledad', 'Atlántico'),
    ('32865681-4', 'Barranquilla', 'Atlántico'),
    ('32908032-0', 'Cartagena De Indias', 'Bolívar'),
    ('32935305-0', 'Cartagena De Indias', 'Bolívar'),
    ('32936518-7', 'Cartagena De Indias', 'Bolívar'),
    ('32937430-2', 'Cartagena De Indias', 'Bolívar'),
    ('32940466-8', 'Arenal', 'Bolívar'),
    ('33026004-2', 'CLEMENCIA', 'Bolívar'),
    ('33100909-9', 'Cartagena De Indias', 'Bolívar'),
    ('33107296-4', 'Cartagena De Indias', 'Bolívar'),
    ('33140319-4', 'Cartagena De Indias', 'Bolívar'),
    ('33153458-6', 'Cartagena De Indias', 'Bolívar'),
    ('33157233-4', 'Cartagena De Indias', 'Bolívar'),
    ('33205415-4', 'Corozal', 'Sucre'),
    ('33248886-4', 'Cartagena De Indias', 'Bolívar'),
    ('33253487-9', 'Cartagena De Indias', 'Bolívar'),
    ('33253579-8', 'Turbaná', 'Bolívar'),
    ('33253788-0', 'Cartagena De Indias', 'Bolívar'),
    ('33262361-8', 'Villanueva', 'Santander'),
    ('33286494-2', 'Cartagena De Indias', 'Bolívar'),
    ('33332986-1', 'Cartagena De Indias', 'Bolívar'),
    ('33358444-4', 'Cartagena De Indias', 'Bolívar'),
    ('33994216-9', 'Ciénaga De Oro', 'Córdoba'),
    ('34065214-2', 'La Virginia', 'Risaralda'),
    ('34549656-2', 'Popayán', 'Cauca'),
    ('34553284-1', 'Santander De Quilichao', 'Cauca'),
    ('34940796-0', 'Sincelejo', 'Sucre'),
    ('3572769-1', 'Yarumal', 'Antioquia'),
    ('35830857-3', 'Istmina', 'Chocó'),
    ('3607806-7', 'Cartagena De Indias', 'Bolívar'),
    ('36380574-6', 'Cúcuta', 'Norte De Santander'),
    ('36506028-1', 'Sabanalarga', 'Atlántico'),
    ('36518969-7', 'Ocaña', 'Norte De Santander'),
    ('36587868-6', 'Pailitas', 'Cesar'),
    ('37013562-9', 'Cúcuta', 'Norte De Santander'),
    ('37082586-1', 'Pasto', 'Nariño'),
    ('37160055-5', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('37161062-1', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('37229997-7', 'San Andrés De Tumaco', 'Nariño'),
    ('37244756-1', 'Cúcuta', 'Norte De Santander')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('37278093-3', 'Cúcuta', 'Norte De Santander'),
    ('37292343-8', 'Cúcuta', 'Norte De Santander'),
    ('37325002-5', 'Ocaña', 'Norte De Santander'),
    ('37334327-1', 'Ocaña', 'Norte De Santander'),
    ('37336729-8', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('37395363-8', 'Villa Del Rosario', 'Norte De Santander'),
    ('37557766-9', 'Villa Del Rosario', 'Norte De Santander'),
    ('37710606-4', 'Saravena', 'Arauca'),
    ('3805711-5', 'Cartagena De Indias', 'Bolívar'),
    ('3828471-1', 'Cartagena De Indias', 'Bolívar'),
    ('38864227-0', 'Guadalajara De Buga', 'Valle Del Cauca'),
    ('38876913-7', 'Guadalajara De Buga', 'Valle Del Cauca'),
    ('39012672-3', 'Cartagena De Indias', 'Bolívar'),
    ('39276393-6', 'Sincelejo', 'Sucre'),
    ('39280680-0', 'Tolú Viejo', 'Sucre'),
    ('39282157-9', 'Caucasia', 'Antioquia'),
    ('39426833-1', 'Quibdó', 'Chocó'),
    ('39564529-6', 'Girardot', 'Cundinamarca'),
    ('40048694-4', 'Tunja', 'Boyacá'),
    ('40189010-1', 'Villavicencio', 'Meta'),
    ('40418386-9', 'Puerto Gaitán', 'Meta'),
    ('40436611-8', 'Cúcuta', 'Norte De Santander'),
    ('40448228', 'Granada', 'Meta'),
    ('40691341-6', 'San Vicente Del Caguán', 'Caquetá'),
    ('40771066-9', 'Cartagena Del Chairá', 'Caquetá'),
    ('408636140', 'Caracas', 'Venezuela'),
    ('40877393-9', 'Cartagena De Indias', 'Bolívar'),
    ('410077174', 'Caracas', 'Venezuela'),
    ('41181552-9', 'Pasca', 'Cundinamarca'),
    ('41705022', 'Ibagué', 'Tolima'),
    ('41794909-5', 'Armenia', 'Quindío'),
    ('41905478-0', 'Armenia', 'Quindío'),
    ('41924846-9', 'Armenia', 'Quindío'),
    ('41944089', 'Armenia', 'Quindío'),
    ('41949256', 'Ansermanuevo', 'Valle Del Cauca'),
    ('41953236-1', 'Armenia', 'Quindío'),
    ('41954876-8', 'Armenia', 'Quindío'),
    ('41962911-1', 'Armenia', 'Quindío'),
    ('42014709-7', 'Santa Marta', 'Magdalena'),
    ('42014921', 'Pereira', 'Risaralda'),
    ('42032121-3', 'La Virginia', 'Risaralda'),
    ('42058956-9', 'Villa Del Rosario', 'Norte De Santander'),
    ('42146331-3', 'Pereira', 'Risaralda'),
    ('42149654-0', 'Dosquebradas', 'Risaralda'),
    ('42271404-7', 'Los Palmitos', 'Sucre'),
    ('43113015-2', 'Cartagena De Indias', 'Bolívar'),
    ('43271267-8', 'Medellín', 'Antioquia'),
    ('43402636-6', 'Chinú', 'Córdoba'),
    ('43402688-9', 'Cartagena De Indias', 'Bolívar'),
    ('43468799-1', 'Sahagún', 'Córdoba')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('43472641-2', 'Galapa', 'Atlántico'),
    ('43653340-8', 'Girardota', 'Antioquia'),
    ('4373676-3', 'La Tebaida', 'Quindío'),
    ('4376499-1', 'Circasia', 'Quindío'),
    ('43786561-1', 'Cartagena De Indias', 'Bolívar'),
    ('43787136-7', 'Cartagena De Indias', 'Bolívar'),
    ('43787356-0', 'Magangué', 'Bolívar'),
    ('43810839', 'Sabaneta', 'Antioquia'),
    ('43865142-6', 'Cartagena De Indias', 'Bolívar'),
    ('4404215-6', 'Pasto', 'Nariño'),
    ('4518652-1', 'Pereira', 'Risaralda'),
    ('4528322-9', 'Armenia', 'Antioquia'),
    ('45360968-1', 'Santa Rosa De Cabal', 'Risaralda'),
    ('45369349-3', 'Cartagena De Indias', 'Bolívar'),
    ('45373714-4', 'María La Baja', 'Bolívar'),
    ('45400241-9', 'Cartagena De Indias', 'Bolívar'),
    ('45433465-3', 'Cartagena De Indias', 'Bolívar'),
    ('45440196-6', 'Cartagena De Indias', 'Bolívar'),
    ('45473780-1', 'Cartagena De Indias', 'Bolívar'),
    ('45486542-1', 'Cartagena De Indias', 'Bolívar'),
    ('45494648-5', 'Cartagena De Indias', 'Bolívar'),
    ('45496797-3', 'Cartagena De Indias', 'Bolívar'),
    ('45499288-1', 'Cartagena De Indias', 'Bolívar'),
    ('45507542-1', 'Cartagena De Indias', 'Bolívar'),
    ('45513014-9', 'Cartagena De Indias', 'Bolívar'),
    ('45523017-3', 'Cartagena De Indias', 'Bolívar'),
    ('45524254-7', 'Cartagena De Indias', 'Bolívar'),
    ('45524842-8', 'Cartagena De Indias', 'Bolívar'),
    ('45527949-0', 'Cartagena De Indias', 'Bolívar'),
    ('45532724-0', 'Cartagena De Indias', 'Bolívar'),
    ('45533354-3', 'Cartagena De Indias', 'Bolívar'),
    ('45542038-9', 'Cartagena De Indias', 'Bolívar'),
    ('45546637-9', 'Cartagena De Indias', 'Bolívar'),
    ('45557101-0', 'Cartagena De Indias', 'Bolívar'),
    ('45558050-8', 'Cartagena De Indias', 'Bolívar'),
    ('45559940-2', 'Cartagena De Indias', 'Bolívar'),
    ('45561392-2', 'Cartagena De Indias', 'Bolívar'),
    ('45579148-0', 'Arjona', 'Bolívar'),
    ('45579315-4', 'El Carmen De Bolívar', 'Bolívar'),
    ('45581835-9', 'Turbaco', 'Bolívar'),
    ('45582050-9', 'El Carmen De Bolívar', 'Bolívar'),
    ('45584501-8', 'El Carmen De Bolívar', 'Bolívar'),
    ('45586624-4', 'Santa Rosa De Cabal', 'Risaralda'),
    ('45593379-3', 'Turbaco', 'Bolívar'),
    ('45685101-9', 'Cartagena De Indias', 'Bolívar'),
    ('45685660-4', 'Cartagena De Indias', 'Bolívar'),
    ('45687066-8', 'Cartagena De Indias', 'Bolívar'),
    ('45687814-0', 'Cartagena De Indias', 'Bolívar'),
    ('45689804-6', 'Cartagena De Indias', 'Bolívar'),
    ('45693736-9', 'Cartagena De Indias', 'Bolívar')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('45694165-8', 'Cartagena De Indias', 'Bolívar'),
    ('45744931-9', 'Villanueva', 'Santander'),
    ('45747921-9', 'Cartagena De Indias', 'Bolívar'),
    ('45756300-3', 'Cartagena De Indias', 'Bolívar'),
    ('45760400-7', 'Cartagena De Indias', 'Bolívar'),
    ('45766399-4', 'Cartagena De Indias', 'Bolívar'),
    ('45767217-7', 'Cartagena De Indias', 'Bolívar'),
    ('4752000-1', 'Rosas', 'Cauca'),
    ('4794356-8', 'Riosucio', 'Caldas'),
    ('4799933-0', 'Nóvita', 'Chocó'),
    ('49652657-4', 'Cúcuta', 'Norte De Santander'),
    ('49763706-3', 'El Banco', 'Magdalena'),
    ('50956411-9', 'Santiago De Tolú', 'Sucre'),
    ('50957889-1', 'Santiago De Tolú', 'Sucre'),
    ('51756689', 'Cúcuta', 'Norte De Santander'),
    ('52002544-3', 'San Luis De Sincé', 'Sucre'),
    ('5204357-9', 'Villa Del Rosario', 'Norte De Santander'),
    ('5210591-0', 'San Pablo', 'Nariño'),
    ('52146049-7', 'San Andrés De Sotavento', 'Córdoba'),
    ('52401720-5', 'Cartagena De Indias', 'Bolívar'),
    ('52832155-2', 'Cúcuta', 'Norte De Santander'),
    ('5472238-9', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('5488280-9', 'Cúcuta', 'Norte De Santander'),
    ('5532263-1', 'Cúcuta', 'Norte De Santander'),
    ('5595803-9', 'Luruaco', 'Atlántico'),
    ('57140390-5', 'Santa Marta', 'Magdalena'),
    ('57423291-1', 'Guamo', 'Tolima'),
    ('5796974-2', 'Bucaramanga', 'Santander'),
    ('5824694-6', 'Armenia', 'Quindío'),
    ('5835829-0', 'Popayán', 'Cauca'),
    ('59121353-9', 'Popayán', 'Cauca'),
    ('59177738-1', 'Jamundí', 'Valle Del Cauca'),
    ('5968426-7', 'Ibagué', 'Tolima'),
    ('59706405-5', 'La Unión', 'Antioquia'),
    ('5973472-6', 'Cartagena De Indias', 'Bolívar'),
    ('59771971425', 'Panamá', 'Panamá'),
    ('5994615-2', 'Rovira', 'Tolima'),
    ('60265786-6', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('60277418-2', 'Cúcuta', 'Norte De Santander'),
    ('60311912-5', 'Cúcuta', 'Norte De Santander'),
    ('60316213-8', 'Cúcuta', 'Norte De Santander'),
    ('60335523-7', 'Villa Del Rosario', 'Norte De Santander'),
    ('60370164', 'Cúcuta', 'Norte De Santander'),
    ('60374880-8', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('60378900-5', 'Cúcuta', 'Norte De Santander'),
    ('60388320-6', 'Cúcuta', 'Norte De Santander'),
    ('60391832-6', 'Cúcuta', 'Norte De Santander'),
    ('60395842', 'Cúcuta', 'Norte De Santander'),
    ('60406902-0', 'Villa Del Rosario', 'Norte De Santander'),
    ('60413300-6', 'Villa Del Rosario', 'Norte De Santander')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('60450298-6', 'Villa Del Rosario', 'Norte De Santander'),
    ('612211324', 'Estados Unidos', 'Estados Unidos'),
    ('6211841-5', 'Montería', 'Córdoba'),
    ('6241345-1', 'Cartago', 'Valle Del Cauca'),
    ('6281173-2', 'Cartagena De Indias', 'Bolívar'),
    ('63509399-9', 'Bucaramanga', 'Santander'),
    ('63553437-7', 'Cúcuta', 'Norte De Santander'),
    ('6382420-0', 'Armenia', 'Quindío'),
    ('64521391-3', 'Cartagena De Indias', 'Bolívar'),
    ('64540542-1', 'Sincelejo', 'Sucre'),
    ('64552434-4', 'Cartagena De Indias', 'Bolívar'),
    ('64563749-6', 'Sincelejo', 'Sucre'),
    ('64563753-6', 'Sincelejo', 'Sucre'),
    ('64567472-1', 'Sincelejo', 'Sucre'),
    ('64574958-6', 'Sampués', 'Sucre'),
    ('64582316-1', 'Sincelejo', 'Sucre'),
    ('64584132-2', 'Santa Marta', 'Magdalena'),
    ('64586133-9', 'Sincelejo', 'Sucre'),
    ('64697479-8', 'Sincelejo', 'Sucre'),
    ('64698261-4', 'Sincelejo', 'Sucre'),
    ('64702118-6', 'Sincelejo', 'Sucre'),
    ('64704725-6', 'San Andrés De Sotavento', 'Córdoba'),
    ('64719388-2', 'Sampués', 'Sucre'),
    ('64749182-0', 'San Benito Abad', 'Sucre'),
    ('64749372-3', 'San Benito Abad', 'Sucre'),
    ('64871253-6', 'Buenavista', 'Boyacá'),
    ('64891376-9', 'Ovejas', 'Sucre'),
    ('64893876-9', 'Arjona', 'Bolívar'),
    ('64895131-1', 'Ovejas', 'Sucre'),
    ('65747239-6', 'Ibagué', 'Tolima'),
    ('6618242-1', 'Chinú', 'Córdoba'),
    ('66904535-9', 'Cali', 'Valle Del Cauca'),
    ('66909326-9', 'Cúcuta', 'Norte De Santander'),
    ('6693600-4', 'Pitalito', 'Huila'),
    ('67021136-7', 'Villa Del Rosario', 'Norte De Santander'),
    ('6766816-2', 'Tunja', 'Boyacá'),
    ('6776531-1', 'Tunja', 'Boyacá'),
    ('6794580-9', 'Villa Del Rosario', 'Norte De Santander'),
    ('6797576-2', 'Guaranda', 'Sucre'),
    ('6888220-7', 'Cartagena De Indias', 'Bolívar'),
    ('69028700-0', 'Popayán', 'Cauca'),
    ('700072297', 'Pereira', 'Risaralda'),
    ('700110060-3', 'Popayán', 'Cauca'),
    ('700280766-2', 'Arauca', 'Arauca'),
    ('700379130-6', 'Armenia', 'Quindío'),
    ('700402035-2', 'Cartagena De Indias', 'Bolívar'),
    ('700430205-7', 'Cúcuta', 'Norte De Santander'),
    ('700447615-8', 'Cartagena De Indias', 'Bolívar'),
    ('700513571-5', 'Villa Del Rosario', 'Norte De Santander'),
    ('70073989-6', 'Turbaco', 'Bolívar')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('70137228-6', 'Chigorodó', 'Antioquia'),
    ('70256714-4', 'Cartagena De Indias', 'Bolívar'),
    ('70381641-1', 'Cúcuta', 'Norte De Santander'),
    ('70382697-6', 'Cartagena De Indias', 'Bolívar'),
    ('70386026-2', 'Cartagena De Indias', 'Bolívar'),
    ('70465682-3', 'El Carmen De Bolívar', 'Bolívar'),
    ('70693125-1', 'Sincelejo', 'Sucre'),
    ('70693270-1', 'Montenegro', 'Quindío'),
    ('70695098-8', 'Cartagena De Indias', 'Bolívar'),
    ('70695458-6', 'San Onofre', 'Sucre'),
    ('70697123-3', 'San Onofre', 'Sucre'),
    ('70697301-8', 'Ovejas', 'Sucre'),
    ('70900882-6', 'Sahagún', 'Córdoba'),
    ('70903631-8', 'Cartagena De Indias', 'Bolívar'),
    ('70954711-7', 'Cartagena De Indias', 'Bolívar'),
    ('71003323-6', 'Medellín', 'Antioquia'),
    ('71082604-8', 'Armenia', 'Quindío'),
    ('71317542-0', 'Medellín', 'Antioquia'),
    ('71451673-1', 'Maicao', 'La Guajira'),
    ('71638471', 'La Tebaida', 'Quindío'),
    ('71761851-4', 'Medellín', 'Antioquia'),
    ('71940777-5', 'Sincelejo', 'Sucre'),
    ('72018711-1', 'Barranquilla', 'Atlántico'),
    ('72021789-4', 'Repelón', 'Atlántico'),
    ('72072819-5', 'Luruaco', 'Atlántico'),
    ('72172596-7', 'Cartagena De Indias', 'Bolívar'),
    ('72222391-1', 'Armenia', 'Quindío'),
    ('72243978-2', 'Cartagena De Indias', 'Bolívar'),
    ('72295533-1', 'Cartagena De Indias', 'Bolívar'),
    ('72335727-6', 'Sabanagrande', 'Atlántico'),
    ('73108216-5', 'Cartagena De Indias', 'Bolívar'),
    ('73109497-2', 'Cartagena De Indias', 'Bolívar'),
    ('73117774-1', 'Cartagena De Indias', 'Bolívar'),
    ('73120621-4', 'Cartagena De Indias', 'Bolívar'),
    ('73122584-9', 'Cartagena De Indias', 'Bolívar'),
    ('73124246-3', 'Cartagena De Indias', 'Bolívar'),
    ('73127496-1', 'Cartagena De Indias', 'Bolívar'),
    ('73129157-9', 'Cartagena Del Chairá', 'Caquetá'),
    ('73135037-8', 'Cartagena De Indias', 'Bolívar'),
    ('73140106-8', 'Cartagena De Indias', 'Bolívar'),
    ('73140180-3', 'Cartagena De Indias', 'Bolívar'),
    ('7316101-9', 'Chiquinquirá', 'Boyacá'),
    ('73163929-1', 'Mahates', 'Bolívar'),
    ('73183016-8', 'Turbaná', 'Bolívar'),
    ('73197228-3', 'Cartagena De Indias', 'Bolívar'),
    ('73203722', 'Cartagena De Indias', 'Bolívar'),
    ('73203750-3', 'Cartagena De Indias', 'Bolívar'),
    ('73206722-0', 'Cartagena De Indias', 'Bolívar'),
    ('73214382', 'Cartagena De Indias', 'Bolívar'),
    ('73214399-8', 'Cartagena De Indias', 'Bolívar')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('73214446-6', 'Cartagena De Indias', 'Bolívar'),
    ('73214834-0', 'Cartagena De Indias', 'Bolívar'),
    ('73228118-6', 'Cartagena De Indias', 'Bolívar'),
    ('73228383-1', 'San Juan Nepomuceno', 'Bolívar'),
    ('7333997-2', 'Cartagena De Indias', 'Bolívar'),
    ('73429956-4', 'El Carmen De Bolívar', 'Bolívar'),
    ('73433253-0', 'El Carmen De Bolívar', 'Bolívar'),
    ('73543566-2', 'Turbaco', 'Bolívar'),
    ('73556156-2', 'Arjona', 'Bolívar'),
    ('73575900-7', 'Cartagena De Indias', 'Bolívar'),
    ('73581693-1', 'Cartagena De Indias', 'Bolívar'),
    ('73593783-8', 'Cartagena De Indias', 'Bolívar'),
    ('7382686-6', 'Santa Rosa De Cabal', 'Risaralda'),
    ('7383471-4', 'Ibagué', 'Tolima'),
    ('74372387-1', 'Duitama', 'Boyacá'),
    ('75037220-7', 'Riosucio', 'Caldas'),
    ('75046246-6', 'Manizales', 'Caldas'),
    ('75068840-6', 'Salamina', 'Caldas'),
    ('75069171-1', 'Arauca', 'Arauca'),
    ('75079952-1', 'Manizales', 'Caldas'),
    ('7521430-5', 'Ibagué', 'Tolima'),
    ('77015112-2', 'Pivijay', 'Magdalena'),
    ('77140363-9', 'Curumaní', 'Cesar'),
    ('77176196-0', 'Cúcuta', 'Norte De Santander'),
    ('77194210-2', 'Santa Marta', 'Magdalena'),
    ('77985988', 'Chile', 'Chile'),
    ('78023258-6', 'Lorica', 'Córdoba'),
    ('78380860-0', 'San Andrés De Sotavento', 'Córdoba'),
    ('78673791-9', 'Chinú', 'Córdoba'),
    ('78691910-5', 'Lorica', 'Córdoba'),
    ('78698170-3', 'Caucasia', 'Antioquia'),
    ('78734038-3', 'Montería', 'Córdoba'),
    ('78734366-4', 'Chinú', 'Córdoba'),
    ('78734530-6', 'Chinú', 'Córdoba'),
    ('78739073-4', 'Santiago De Tolú', 'Sucre'),
    ('78739239-1', 'Cartagena De Indias', 'Bolívar'),
    ('7927965-7', 'San Juan Nepomuceno', 'Bolívar'),
    ('79334680-1', 'Cartagena De Indias', 'Bolívar'),
    ('79400660-5', 'Turbaco', 'Bolívar'),
    ('79454062-2', 'Riohacha', 'La Guajira'),
    ('7959567-6', 'Cartagena De Indias', 'Bolívar'),
    ('7960009-1', 'Cartagena De Indias', 'Bolívar'),
    ('79713694-9', 'Timbío', 'Cauca'),
    ('80026539-1', 'Santo Tomás', 'Atlántico'),
    ('80108329-4', 'La Tebaida', 'Quindío'),
    ('8056481-1', 'Tarazá', 'Antioquia'),
    ('80815278-1', 'Guamal', 'Magdalena'),
    ('823001661-9', 'Colosó', 'Sucre'),
    ('8259139-7', 'Medellín', 'Antioquia'),
    ('830511845-0', 'Cúcuta', 'Norte De Santander')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('84075910-5', 'Baranoa', 'Atlántico'),
    ('84091221-6', 'Riohacha', 'La Guajira'),
    ('8419841-7', 'Medellín', 'Antioquia'),
    ('8437285-8', 'Carepa', 'Antioquia'),
    ('8527351-2', 'San Juan Nepomuceno', 'Bolívar'),
    ('85457635-8', 'Cartagena De Indias', 'Bolívar'),
    ('8600784-1', 'Cartagena De Indias', 'Bolívar'),
    ('86010193-9', 'Granada', 'Antioquia'),
    ('8601778-1', 'Cartagena De Indias', 'Bolívar'),
    ('8604073-1', 'Repelón', 'Atlántico'),
    ('8634756-1', 'Sabanalarga', 'Atlántico'),
    ('8638049-9', 'Sabanalarga', 'Atlántico'),
    ('8641509-6', 'Sabanalarga', 'Atlántico'),
    ('87012359932-1', 'Cúcuta', 'Norte De Santander'),
    ('87029793-5', 'Popayán', 'Cauca'),
    ('87940107-9', 'San Andrés De Tumaco', 'Nariño'),
    ('88027556-0', 'Cúcuta', 'Norte De Santander'),
    ('88132478-3', 'Villa Del Rosario', 'Norte De Santander'),
    ('88149954-2', 'Cúcuta', 'Norte De Santander'),
    ('88173788-7', 'Tibú', 'Norte De Santander'),
    ('88176099-4', 'Tibú', 'Norte De Santander'),
    ('88178492-5', 'Salazar De Las Palmas', 'Norte De Santander'),
    ('88178626-5', 'Cúcuta', 'Norte De Santander'),
    ('88192543-0', 'Villa Del Rosario', 'Norte De Santander'),
    ('88193059-1', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('88193426-1', 'Villa Del Rosario', 'Norte De Santander'),
    ('88194597-7', 'Villa Del Rosario', 'Norte De Santander'),
    ('88194911-7', 'Villa Del Rosario', 'Norte De Santander'),
    ('88195072-7', 'Cúcuta', 'Norte De Santander'),
    ('88195510-1', 'Villa Del Rosario', 'Norte De Santander'),
    ('88198519-0', 'Los Patios', 'Norte De Santander'),
    ('88198633-2', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('88200901-1', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('88210501-1', 'Cúcuta', 'Norte De Santander'),
    ('8821052-5', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('88211803-3', 'Cúcuta', 'Norte De Santander'),
    ('88212311-6', 'Cúcuta', 'Norte De Santander'),
    ('88213346-8', 'Cúcuta', 'Norte De Santander'),
    ('88213350-8', 'El Tarra', 'Norte De Santander'),
    ('88224406-9', 'Cúcuta', 'Norte De Santander'),
    ('88226411-5', 'Villa Del Rosario', 'Norte De Santander'),
    ('88232250-0', 'Cúcuta', 'Norte De Santander'),
    ('88235406-6', 'Cúcuta', 'Norte De Santander'),
    ('88261571-3', 'Cúcuta', 'Norte De Santander'),
    ('88277944-7', 'Cúcuta', 'Norte De Santander'),
    ('88287667-4', 'Arjona', 'Bolívar'),
    ('8852686-7', 'Cartagena De Indias', 'Bolívar'),
    ('89000682-7', 'Armenia', 'Quindío'),
    ('89001867-7', 'Armenia', 'Quindío'),
    ('89002053-3', 'Calarcá', 'Quindío')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('89009010-9', 'Armenia', 'Quindío'),
    ('900183057-9', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('900245671-9', 'Cúcuta', 'Norte De Santander'),
    ('900314952-1', 'Itagüí', 'Antioquia'),
    ('900405809-5', 'Cartagena De Indias', 'Bolívar'),
    ('900495259-9', 'Orito', 'Putumayo'),
    ('900517010-9', 'Sincelejo', 'Sucre'),
    ('900540026-2', 'El Carmen De Bolívar', 'Bolívar'),
    ('900599145-5', 'Bello', 'Antioquia'),
    ('900599688-2', 'San Sebastián De Buenavista', 'Magdalena'),
    ('900672346-0', 'Armenia', 'Quindío'),
    ('900688089-2', 'Barranquilla', 'Atlántico'),
    ('900744350-0', 'Sincelejo', 'Sucre'),
    ('900772161-4', 'Cartagena De Indias', 'Bolívar'),
    ('900823725-8', 'Cúcuta', 'Norte De Santander'),
    ('900840844-8', 'Villa Del Rosario', 'Norte De Santander'),
    ('900919187-9', 'Cartagena De Indias', 'Bolívar'),
    ('900920026-3', 'Cartagena De Indias', 'Bolívar'),
    ('900922612-9', 'Villa Del Rosario', 'Norte De Santander'),
    ('900959079-2', 'Cúcuta', 'Norte De Santander'),
    ('900964845-8', 'Armenia', 'Quindío'),
    ('900988758-9', 'Barranquilla', 'Atlántico'),
    ('901082765-5', 'Itagüí', 'Antioquia'),
    ('901084571-2', 'San Juan Del Cesar', 'La Guajira'),
    ('901102347-7', 'Cartagena De Indias', 'Bolívar'),
    ('901126813-1', 'La Tebaida', 'Quindío'),
    ('901127779-3', 'Pereira', 'Risaralda'),
    ('901146155-9', 'Medellín', 'Antioquia'),
    ('901150440-9', 'Girardota', 'Antioquia'),
    ('901154313-1', 'Medellín', 'Antioquia'),
    ('901199032-9', 'Florencia', 'Caquetá'),
    ('901223983-0', 'Sincelejo', 'Sucre'),
    ('901238214-0', 'Itagüí', 'Antioquia'),
    ('901245556-3', 'Santander De Quilichao', 'Cauca'),
    ('901248760-3', 'Puerto Carreño', 'Vichada'),
    ('901250776-7', 'Pamplona', 'Norte De Santander'),
    ('901268511-1', 'Neira', 'Caldas'),
    ('901275631-6', 'Cúcuta', 'Norte De Santander'),
    ('901277902', 'Armenia', 'Quindío'),
    ('901278598-4', 'Calarcá', 'Quindío'),
    ('901302150-1', 'San Andrés De Tumaco', 'Nariño'),
    ('901303394-6', 'Nuevo Colón', 'Boyacá'),
    ('901322941-6', 'El Zulia', 'Norte De Santander'),
    ('901338885-1', 'Pamplona', 'Norte De Santander'),
    ('901355584-1', 'Apartadó', 'Antioquia'),
    ('901355670-7', 'Cúcuta', 'Norte De Santander'),
    ('901355763-3', 'Cúcuta', 'Norte De Santander'),
    ('901356519-7', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('901357207-9', 'Cúcuta', 'Norte De Santander'),
    ('901358481-5', 'Duitama', 'Boyacá')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('901366159-1', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('901366204-5', 'Cúcuta', 'Norte De Santander'),
    ('901366452-5', 'Cúcuta', 'Norte De Santander'),
    ('901373054-6', 'Cúcuta', 'Norte De Santander'),
    ('901373245-6', 'Cúcuta', 'Norte De Santander'),
    ('901375361-1', 'Cúcuta', 'Norte De Santander'),
    ('901378461-3', 'Cúcuta', 'Norte De Santander'),
    ('901380175-8', 'Villa Del Rosario', 'Norte De Santander'),
    ('901386298-2', 'Villa Del Rosario', 'Norte De Santander'),
    ('901394543-6', 'San Andrés De Tumaco', 'Nariño'),
    ('901398268-3', 'Pamplona', 'Norte De Santander'),
    ('901399964-6', 'Maicao', 'La Guajira'),
    ('901400070-0', 'Villa Del Rosario', 'Norte De Santander'),
    ('901410092-5', 'Villa Del Rosario', 'Norte De Santander'),
    ('901420038-1', 'La Tebaida', 'Quindío'),
    ('901421464-9', 'Cartagena De Indias', 'Bolívar'),
    ('901426687-7', 'Armenia', 'Quindío'),
    ('901426965-1', 'Barbosa', 'Santander'),
    ('901429039-8', 'Cartagena De Indias', 'Bolívar'),
    ('901429175-1', 'Leticia', 'Amazonas'),
    ('901442603-6', 'Cúcuta', 'Norte De Santander'),
    ('901444514-8', 'Ibagué', 'Tolima'),
    ('901447517-3', 'Pamplona', 'Norte De Santander'),
    ('901450003-0', 'Armenia', 'Quindío'),
    ('901453765-8', 'Tolú Viejo', 'Sucre'),
    ('901454912-9', 'Fonseca', 'La Guajira'),
    ('901458092-2', 'San José Del Guaviare', 'Guaviare'),
    ('901463175-5', 'Cúcuta', 'Norte De Santander'),
    ('901466010-2', 'La Tebaida', 'Quindío'),
    ('901467891-9', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('901480204-2', 'San Cayetano', 'Norte De Santander'),
    ('901482512-5', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('901485064-0', 'Cúcuta', 'Norte De Santander'),
    ('901516943-4', 'San Andrés De Tumaco', 'Nariño'),
    ('901519372-2', 'Villa Del Rosario', 'Norte De Santander'),
    ('901524588-6', 'PUERTO SANTANDER', 'Norte De Santander'),
    ('901529918-6', 'Cartagena De Indias', 'Bolívar'),
    ('901543558-6', 'María La Baja', 'Bolívar'),
    ('901554479-1', 'Maicao', 'La Guajira'),
    ('901561154-0', 'Armenia', 'Quindío'),
    ('901562483-3', 'Calamar', 'Guaviare'),
    ('901569807-8', 'Repelón', 'Atlántico'),
    ('901573230-4', 'Chaparral', 'Tolima'),
    ('901575473-6', 'Cúcuta', 'Norte De Santander'),
    ('901575805-8', 'Sincelejo', 'Sucre'),
    ('901584442-6', 'Sincelejo', 'Sucre'),
    ('901586332-3', 'Bucaramanga', 'Santander'),
    ('901593148-3', 'Ibagué', 'Tolima'),
    ('901594126-6', 'Armenia', 'Quindío'),
    ('901594945-1', 'Ocaña', 'Norte De Santander')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('901595746-7', 'Cúcuta', 'Norte De Santander'),
    ('901597402-8', 'Maicao', 'La Guajira'),
    ('901605368-0', 'Ibagué', 'Tolima'),
    ('901623531-1', 'Riohacha', 'La Guajira'),
    ('901627179-1', 'San Andrés De Tumaco', 'Nariño'),
    ('901632051-6', 'Armenia', 'Quindío'),
    ('901649039-1', 'Cartagena De Indias', 'Bolívar'),
    ('901668959-3', 'Cartagena De Indias', 'Bolívar'),
    ('901670703-1', 'Sabanalarga', 'Atlántico'),
    ('901693891-7', 'Arauca', 'Arauca'),
    ('901714536-9', 'Puerto Leguízamo', 'Putumayo'),
    ('901726274-6', 'Restrepo', 'Meta'),
    ('901727746-5', 'Armenia', 'Quindío'),
    ('901728138-1', 'Cúcuta', 'Norte De Santander'),
    ('901728761-0', 'Salamina', 'Caldas'),
    ('901731289-6', 'Sincelejo', 'Sucre'),
    ('901732294-8', 'Sincelejo', 'Sucre'),
    ('901788781-4', 'Calarcá', 'Quindío'),
    ('901795830-6', 'Sincelejo', 'Sucre'),
    ('901799069-5', 'Yopal', 'Casanare'),
    ('901801661-4', 'Consacá', 'Nariño'),
    ('901813690-1', 'Sabanalarga', 'Casanare'),
    ('901838466-4', 'Cartagena De Indias', 'Bolívar'),
    ('901846481-9', 'Cali', 'Valle Del Cauca'),
    ('901846887-5', 'Valledupar', 'Cesar'),
    ('901847552-8', 'Cúcuta', 'Norte De Santander'),
    ('901852850-8', 'Tuluá', 'Valle Del Cauca'),
    ('901866629-7', 'Rovira', 'Tolima'),
    ('901867249-6', 'San Andrés De Tumaco', 'Nariño'),
    ('901879470-1', 'San Jacinto', 'Bolívar'),
    ('901898586-6', 'Cúcuta', 'Norte De Santander'),
    ('901911009', 'Sincelejo', 'Sucre'),
    ('901913013-2', 'Risaralda', 'Caldas'),
    ('901923715-7', 'Pitalito', 'Huila'),
    ('901932306-6', 'Pereira', 'Risaralda'),
    ('901942525-5', 'Cartagena De Indias', 'Bolívar'),
    ('901955264-4', 'Caicedonia', 'Valle Del Cauca'),
    ('901956827-5', 'Cúcuta', 'Norte De Santander'),
    ('901958506-5', 'Cartagena De Indias', 'Bolívar'),
    ('901966544-9', 'Cartagena De Indias', 'Bolívar'),
    ('901969983-2', 'Chinácota', 'Norte De Santander'),
    ('901981691-6', 'Sabanalarga', 'Antioquia'),
    ('901991164', 'Florencia', 'Caquetá'),
    ('902003269-1', 'Cúcuta', 'Norte De Santander'),
    ('902007161-1', 'El Banco', 'Magdalena'),
    ('902022554-5', 'Pereira', 'Risaralda'),
    ('902041556-0', 'Ovejas', 'Sucre'),
    ('902052010-9', 'Floridablanca', 'Santander'),
    ('902057101-3', 'Lorica', 'Córdoba'),
    ('9086823-7', 'Cartagena De Indias', 'Bolívar')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('9098775-3', 'Turbaco', 'Bolívar'),
    ('9101716-1', 'Cartagena De Indias', 'Bolívar'),
    ('91040661-0', 'Turbaná', 'Bolívar'),
    ('91104857-3', 'Túquerres', 'Nariño'),
    ('91154900-6', 'Bucaramanga', 'Santander'),
    ('91213441-0', 'Arauca', 'Arauca'),
    ('91227024-3', 'Sincelejo', 'Sucre'),
    ('9143719-3', 'Majagual', 'Sucre'),
    ('9145944-3', 'Cartagena De Indias', 'Bolívar'),
    ('91468075-2', 'Arauca', 'Arauca'),
    ('91475565-9', 'Los Patios', 'Norte De Santander'),
    ('9148353-4', 'Cartagena De Indias', 'Bolívar'),
    ('9151164-1', 'María La Baja', 'Bolívar'),
    ('91513630-3', 'Cúcuta', 'Norte De Santander'),
    ('91518126-5', 'Tame', 'Arauca'),
    ('91518427-7', 'Cúcuta', 'Norte De Santander'),
    ('9155481-8', 'Cartagena De Indias', 'Bolívar'),
    ('9176289-1', 'San Jacinto', 'Bolívar'),
    ('92028402-4', 'Sincelejo', 'Sucre'),
    ('92029793-3', 'Galeras', 'Sucre'),
    ('92031302-7', 'San Luis De Sincé', 'Sucre'),
    ('92099892-3', 'Galeras', 'Sucre'),
    ('92127557-1', 'Majagual', 'Sucre'),
    ('92255316-1', 'Sampués', 'Sucre'),
    ('92256193-7', 'Sampués', 'Sucre'),
    ('92277558-1', 'Tolú Viejo', 'Sucre'),
    ('92277604-2', 'Tolú Viejo', 'Sucre'),
    ('92277894-1', 'Santiago De Tolú', 'Sucre'),
    ('92449519-3', 'La Tebaida', 'Quindío'),
    ('92450453-8', 'San Onofre', 'Sucre'),
    ('92497644-0', 'Sincelejo', 'Sucre'),
    ('92499717-9', 'Armenia', 'Quindío'),
    ('92504958-9', 'Corozal', 'Sucre'),
    ('92510673-1', 'Sincelejo', 'Sucre'),
    ('92519673-0', 'Sincelejo', 'Sucre'),
    ('92527761-4', 'El Carmen De Bolívar', 'Bolívar'),
    ('92534750-2', 'Sampués', 'Sucre'),
    ('92536244-6', 'Sincelejo', 'Sucre'),
    ('92540553-2', 'Galeras', 'Sucre'),
    ('92541272-2', 'Sincelejo', 'Sucre'),
    ('92543432-3', 'Sincelejo', 'Sucre'),
    ('92543610-8', 'Sincelejo', 'Sucre'),
    ('92551139-3', 'San Marcos', 'Sucre'),
    ('92552055-8', 'Corozal', 'Sucre'),
    ('92555309-7', 'San Pedro', 'Sucre'),
    ('92601615-3', 'Colosó', 'Sucre'),
    ('92640261-6', 'Sincelejo', 'Sucre'),
    ('92642124-4', 'Sincelejo', 'Sucre'),
    ('92670609-3', 'Sampués', 'Sucre'),
    ('9289050-2', 'Cartagena De Indias', 'Bolívar')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

INSERT INTO customer_addresses (customer_id, city_id, is_main, created_at, updated_at)
SELECT c.id, ct.id, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM (VALUES
    ('9295443-8', 'Turbaco', 'Bolívar'),
    ('9299356-3', 'Cartagena De Indias', 'Bolívar'),
    ('9299704-3', 'Cartagena De Indias', 'Bolívar'),
    ('93121789-1', 'Espinal', 'Tolima'),
    ('93134880-9', 'Espinal', 'Tolima'),
    ('93286302-5', 'Ibagué', 'Tolima'),
    ('93295896-6', 'Yumbo', 'Valle Del Cauca'),
    ('93299466-0', 'Ibagué', 'Tolima'),
    ('93299552-6', 'Líbano', 'Tolima'),
    ('93357878-0', 'Bogotá, D.C.', 'Bogotá D.C.'),
    ('93364982-8', 'Girardot', 'Cundinamarca'),
    ('93388877-6', 'La Florida', 'Nariño'),
    ('93451196-8', 'Armenia', 'Quindío'),
    ('94225949-2', 'Buenaventura', 'Valle Del Cauca'),
    ('94228648-4', 'Dosquebradas', 'Risaralda'),
    ('94252691-2', 'Caicedonia', 'Valle Del Cauca'),
    ('94280493-1', 'Tuluá', 'Valle Del Cauca'),
    ('94283533-1', 'La Tebaida', 'Quindío'),
    ('9433831-5', 'Yopal', 'Casanare'),
    ('94365973-1', 'Tuluá', 'Valle Del Cauca'),
    ('94368828-3', 'Florencia', 'Caquetá'),
    ('94428484-1', 'Cali', 'Valle Del Cauca'),
    ('94459599-2', 'Armenia', 'Quindío'),
    ('94462961-7', 'Caicedonia', 'Valle Del Cauca'),
    ('96123653-7', 'Saravena', 'Arauca'),
    ('96191295-3', 'Villa Del Rosario', 'Norte De Santander'),
    ('9693010-1', 'Aguachica', 'Cesar'),
    ('9729281-6', 'Armenia', 'Quindío'),
    ('9733533', 'La Tebaida', 'Quindío'),
    ('9736909', 'Armenia', 'Quindío'),
    ('9738383-7', 'Armenia', 'Quindío'),
    ('9738868-7', 'Calarcá', 'Quindío'),
    ('97472583-1', 'Sibundoy', 'Putumayo'),
    ('9762019-1', 'Belén De Umbría', 'Risaralda'),
    ('9770230-3', 'Popayán', 'Cauca'),
    ('9772759-6', 'Armenia', 'Quindío'),
    ('9773256-8', 'Armenia', 'Quindío'),
    ('9807238-3', 'La Tebaida', 'Quindío'),
    ('9810156-9', 'Santa Rosa De Lima', 'Bolívar'),
    ('98323795-1', 'San Pablo Sur', 'Bolívar'),
    ('98381503-3', 'Pasto', 'Nariño'),
    ('98398602-9', 'Pasto', 'Nariño'),
    ('98586330-7', 'Cartagena De Indias', 'Bolívar'),
    ('98655726-6', 'Caucasia', 'Antioquia'),
    ('98655876-2', 'Rionegro', 'Antioquia'),
    ('9867341', 'Istmina', 'Chocó'),
    ('98763158-5', 'Medellín', 'Antioquia')
) AS v(document, city_name, dept_name)
JOIN customers c ON c.document = v.document
JOIN departments d ON d.name ILIKE v.dept_name
JOIN cities ct ON ct.name ILIKE v.city_name AND ct.department_id = d.id
ON CONFLICT DO NOTHING;

COMMIT;
