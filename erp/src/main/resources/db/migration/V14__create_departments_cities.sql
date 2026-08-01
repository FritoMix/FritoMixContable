CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE cities (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id BIGINT NOT NULL,
    CONSTRAINT fk_cities_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE CASCADE,
    UNIQUE(name, department_id)
);

INSERT INTO departments (name) VALUES
('Amazonas'),
('Antioquia'),
('Arauca'),
('Atlántico'),
('Bolívar'),
('Boyacá'),
('Caldas'),
('Caquetá'),
('Casanare'),
('Cauca'),
('Cesar'),
('Chocó'),
('Córdoba'),
('Cundinamarca'),
('Guainía'),
('Guaviare'),
('Huila'),
('La Guajira'),
('Magdalena'),
('Meta'),
('Nariño'),
('Norte de Santander'),
('Putumayo'),
('Quindío'),
('Risaralda'),
('San Andrés y Providencia'),
('Santander'),
('Sucre'),
('Tolima'),
('Valle del Cauca'),
('Vaupés'),
('Vichada');

-- Amazonas
INSERT INTO cities (name, department_id) VALUES ('Leticia', 1), ('Puerto Nariño', 1), ('El Encanto', 1);

-- Antioquia
INSERT INTO cities (name, department_id) VALUES ('Medellín', 2), ('Bello', 2), ('Itagüí', 2), ('Envigado', 2), ('Rionegro', 2), ('Apartadó', 2), ('Turbo', 2), ('Puerto Berrío', 2), ('Caucasia', 2), ('Segovia', 2);

-- Arauca
INSERT INTO cities (name, department_id) VALUES ('Arauca', 3), ('Saravena', 3), ('Tame', 3), ('Arauquita', 3);

-- Atlántico
INSERT INTO cities (name, department_id) VALUES ('Barranquilla', 4), ('Soledad', 4), ('Malambo', 4), ('Puerto Colombia', 4), ('Sabanalarga', 4), ('Luruaco', 4);

-- Bolívar
INSERT INTO cities (name, department_id) VALUES ('Cartagena', 5), ('Magangué', 5), ('El Carmen de Bolívar', 5), ('Turbaco', 5), ('Mompós', 5), ('Arjona', 5);

-- Boyacá
INSERT INTO cities (name, department_id) VALUES ('Tunja', 6), ('Sogamoso', 6), ('Duitama', 6), ('Chiquinquirá', 6), ('Paipa', 6), ('Samacá', 6), ('Miraflores', 6);

-- Caldas
INSERT INTO cities (name, department_id) VALUES ('Manizales', 7), ('La Dorada', 7), ('Chinchiná', 7), ('Villamaría', 7), ('Salamina', 7), ('Aguadas', 7), ('Riosucio', 7);

-- Caquetá
INSERT INTO cities (name, department_id) VALUES ('Florencia', 8), ('San Vicente del Caguán', 8), ('Cartagena del Chairá', 8), ('Puerto Rico', 8);

-- Casanare
INSERT INTO cities (name, department_id) VALUES ('Yopal', 9), ('Paz de Ariporo', 9), ('Trinidad', 9), ('Aguazul', 9), ('Villanueva', 9);

-- Cauca
INSERT INTO cities (name, department_id) VALUES ('Popayán', 10), ('Santander de Quilichao', 10), ('Puerto Tejada', 10), ('Miranda', 10), ('Patía (El Bordo)', 10);

-- Cesar
INSERT INTO cities (name, department_id) VALUES ('Valledupar', 11), ('Aguachica', 11), ('Codazzi', 11), ('Bosconia', 11), ('San Alberto', 11), ('La Jagua de Ibirico', 11);

-- Chocó
INSERT INTO cities (name, department_id) VALUES ('Quibdó', 12), ('Istmina', 12), ('Condoto', 12), ('Tadó', 12);

-- Córdoba
INSERT INTO cities (name, department_id) VALUES ('Montería', 13), ('Lorica', 13), ('Sahagún', 13), ('Cereté', 13), ('Ciénaga de Oro', 13), ('Tierralta', 13);

-- Cundinamarca
INSERT INTO cities (name, department_id) VALUES ('Bogotá', 14), ('Soacha', 14), ('Zipaquirá', 14), ('Facatativá', 14), ('Girardot', 14), ('Fusagasugá', 14), ('Chía', 14), ('Cajicá', 14), ('Mosquera', 14), ('Madrid', 14);

-- Guainía
INSERT INTO cities (name, department_id) VALUES ('Inírida', 15), ('Puerto Colombia', 15), ('San Felipe', 15);

-- Guaviare
INSERT INTO cities (name, department_id) VALUES ('San José del Guaviare', 16), ('El Retorno', 16), ('Calamar', 16);

-- Huila
INSERT INTO cities (name, department_id) VALUES ('Neiva', 17), ('Pitalito', 17), ('La Plata', 17), ('Garzón', 17), ('Campoalegre', 17), ('San Agustín', 17);

-- La Guajira
INSERT INTO cities (name, department_id) VALUES ('Riohacha', 18), ('Maicao', 18), ('Uribia', 18), ('Fonseca', 18), ('San Juan del Cesar', 18), ('Dibulla', 18);

-- Magdalena
INSERT INTO cities (name, department_id) VALUES ('Santa Marta', 19), ('Ciénaga', 19), ('El Banco', 19), ('Fundación', 19), ('Plato', 19), ('Aracataca', 19);

-- Meta
INSERT INTO cities (name, department_id) VALUES ('Villavicencio', 20), ('Acacías', 20), ('Granada', 20), ('Puerto López', 20), ('San Martín', 20), ('Cumaral', 20);

-- Nariño
INSERT INTO cities (name, department_id) VALUES ('Pasto', 21), ('Tumaco', 21), ('Ipiales', 21), ('Túquerres', 21), ('La Unión', 21), ('Barbacoas', 21);

-- Norte de Santander
INSERT INTO cities (name, department_id) VALUES ('Cúcuta', 22), ('Ocaña', 22), ('Pamplona', 22), ('Los Patios', 22), ('Villa del Rosario', 22), ('Tibú', 22);

-- Putumayo
INSERT INTO cities (name, department_id) VALUES ('Mocoa', 23), ('Puerto Asís', 23), ('Valle del Guamuez (La Hormiga)', 23), ('Orito', 23);

-- Quindío
INSERT INTO cities (name, department_id) VALUES ('Armenia', 24), ('Calarcá', 24), ('Montenegro', 24), ('Quimbaya', 24), ('Salento', 24);

-- Risaralda
INSERT INTO cities (name, department_id) VALUES ('Pereira', 25), ('Dosquebradas', 25), ('Santa Rosa de Cabal', 25), ('La Virginia', 25);

-- San Andrés y Providencia
INSERT INTO cities (name, department_id) VALUES ('San Andrés', 26), ('Providencia', 26);

-- Santander
INSERT INTO cities (name, department_id) VALUES ('Bucaramanga', 27), ('Barrancabermeja', 27), ('San Gil', 27), ('Socorro', 27), ('Piedecuesta', 27), ('Floridablanca', 27), ('Girón', 27);

-- Sucre
INSERT INTO cities (name, department_id) VALUES ('Sincelejo', 28), ('Corozal', 28), ('Tolú', 28), ('San Marcos', 28), ('San Benito Abad', 28);

-- Tolima
INSERT INTO cities (name, department_id) VALUES ('Ibagué', 29), ('Espinal', 29), ('Honda', 29), ('Mariquita', 29), ('Melgar', 29), ('Líbano', 29), ('Chaparral', 29);

-- Valle del Cauca
INSERT INTO cities (name, department_id) VALUES ('Cali', 30), ('Buenaventura', 30), ('Palmira', 30), ('Tuluá', 30), ('Cartago', 30), ('Buga', 30), ('Yumbo', 30), ('Jamundí', 30), ('Roldanillo', 30);

-- Vaupés
INSERT INTO cities (name, department_id) VALUES ('Mitú', 31), ('Carurú', 31), ('Taraira', 31);

-- Vichada
INSERT INTO cities (name, department_id) VALUES ('Puerto Carreño', 32), ('La Primavera', 32), ('Santa Rosalía', 32), ('Cumaribo', 32);

ALTER TABLE customer_addresses DROP COLUMN address;
ALTER TABLE customer_addresses DROP COLUMN city;
ALTER TABLE customer_addresses DROP COLUMN department;
ALTER TABLE customer_addresses ADD COLUMN city_id BIGINT NOT NULL;
ALTER TABLE customer_addresses ADD CONSTRAINT fk_customer_addresses_city FOREIGN KEY (city_id) REFERENCES cities(id);

ALTER TABLE customers ADD COLUMN address VARCHAR(250);
