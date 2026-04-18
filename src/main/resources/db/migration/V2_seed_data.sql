-- =============================================
-- V2 — Datos iniciales
-- =============================================

-- Admin (password: pass1234)
INSERT INTO administrators (id, name, email, password_hash, permission, active, created_at, version)
VALUES (
           gen_random_uuid(),
           'admin',
           'admin@huerto.com',
           '$2a$12$YNvEZ.X.5MnNX4A.IFq7Delwe1s7XZ.UPfpxDNGqR0i3zF18JyM0C',
           'OWNER',
           true,
           NOW(),
           0
       );

-- Cliente por defecto (password: pass1234)
INSERT INTO customers (id, name, email, password_hash, phone_country_code, phone_number, created_at, version)
VALUES (
           gen_random_uuid(),
           'User',
           'user@huerto.com',
           '$2a$12$YNvEZ.X.5MnNX4A.IFq7Delwe1s7XZ.UPfpxDNGqR0i3zF18JyM0C',
           '+34',
           '000000000',
           NOW(),
           0
       );

-- =============================================
-- Variedades
-- =============================================

INSERT INTO varieties (id, name, product_category) VALUES
                                                       (gen_random_uuid(), 'Raf',             'Tomato'),
                                                       (gen_random_uuid(), 'Cherry',          'Tomato'),
                                                       (gen_random_uuid(), 'Corazón de buey', 'Tomato'),
                                                       (gen_random_uuid(), 'Mutxamel',        'Tomato'),
                                                       (gen_random_uuid(), 'Rosa de Altea',   'Tomato'),
                                                       (gen_random_uuid(), 'Pera',            'Tomato'),
                                                       (gen_random_uuid(), 'Morrón rojo',     'Pepper'),
                                                       (gen_random_uuid(), 'Morrón verde',    'Pepper'),
                                                       (gen_random_uuid(), 'Italiano',        'Pepper'),
                                                       (gen_random_uuid(), 'Padrón',          'Pepper'),
                                                       (gen_random_uuid(), 'Común',           'Eggplant'),
                                                       (gen_random_uuid(), 'Rayada',          'Eggplant'),
                                                       (gen_random_uuid(), 'Verde',           'Zucchini'),
                                                       (gen_random_uuid(), 'Amarillo',        'Zucchini'),
                                                       (gen_random_uuid(), 'Romana',          'Lettuce'),
                                                       (gen_random_uuid(), 'Hoja de roble',   'Lettuce'),
                                                       (gen_random_uuid(), 'Iceberg',         'Lettuce'),
                                                       (gen_random_uuid(), 'Blanca',          'Onion'),
                                                       (gen_random_uuid(), 'Morada',          'Onion'),
                                                       (gen_random_uuid(), 'Tierna',          'Onion'),
                                                       (gen_random_uuid(), 'Blanco',          'Garlic'),
                                                       (gen_random_uuid(), 'Morado',          'Garlic'),
                                                       (gen_random_uuid(), 'Común',           'Cucumber'),
                                                       (gen_random_uuid(), 'Mini',            'Cucumber'),
                                                       (gen_random_uuid(), 'Común',           'Basil'),
                                                       (gen_random_uuid(), 'Fresco',          'Parsley'),
                                                       (gen_random_uuid(), 'Fresco',          'Rosemary');

-- =============================================
-- Productos
-- =============================================

-- Tomates (disponibles)
INSERT INTO products (id, name, variety_id, price, currency, unit, stock, available, version) VALUES
                                                                                                  (gen_random_uuid(), 'Tomate Raf',            (SELECT id FROM varieties WHERE name = 'Raf'             AND product_category = 'Tomato'), 2.50, 'EUR', 'KG', 100, true,  0),
                                                                                                  (gen_random_uuid(), 'Tomate Cherry',          (SELECT id FROM varieties WHERE name = 'Cherry'          AND product_category = 'Tomato'), 3.00, 'EUR', 'KG', 80,  true,  0),
                                                                                                  (gen_random_uuid(), 'Tomate Corazón de buey', (SELECT id FROM varieties WHERE name = 'Corazón de buey' AND product_category = 'Tomato'), 3.50, 'EUR', 'KG', 60,  true,  0),
                                                                                                  (gen_random_uuid(), 'Tomate Mutxamel',        (SELECT id FROM varieties WHERE name = 'Mutxamel'        AND product_category = 'Tomato'), 2.80, 'EUR', 'KG', 90,  true,  0),
                                                                                                  (gen_random_uuid(), 'Tomate Rosa de Altea',   (SELECT id FROM varieties WHERE name = 'Rosa de Altea'   AND product_category = 'Tomato'), 4.00, 'EUR', 'KG', 50,  true,  0),
                                                                                                  (gen_random_uuid(), 'Tomate Pera',            (SELECT id FROM varieties WHERE name = 'Pera'            AND product_category = 'Tomato'), 2.20, 'EUR', 'KG', 120, true,  0);

-- Pimientos (sin stock)
INSERT INTO products (id, name, variety_id, price, currency, unit, stock, available, version) VALUES
                                                                                                  (gen_random_uuid(), 'Pimiento Morrón Rojo',  (SELECT id FROM varieties WHERE name = 'Morrón rojo'  AND product_category = 'Pepper'), 2.20, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Pimiento Morrón Verde', (SELECT id FROM varieties WHERE name = 'Morrón verde' AND product_category = 'Pepper'), 1.80, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Pimiento Italiano',     (SELECT id FROM varieties WHERE name = 'Italiano'     AND product_category = 'Pepper'), 2.00, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Pimiento de Padrón',    (SELECT id FROM varieties WHERE name = 'Padrón'       AND product_category = 'Pepper'), 3.50, 'EUR', 'KG', 0, false, 0);

-- Berenjenas (sin stock)
INSERT INTO products (id, name, variety_id, price, currency, unit, stock, available, version) VALUES
                                                                                                  (gen_random_uuid(), 'Berenjena Común',  (SELECT id FROM varieties WHERE name = 'Común'  AND product_category = 'Eggplant'), 1.80, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Berenjena Rayada', (SELECT id FROM varieties WHERE name = 'Rayada' AND product_category = 'Eggplant'), 2.20, 'EUR', 'KG', 0, false, 0);

-- Calabacines (sin stock)
INSERT INTO products (id, name, variety_id, price, currency, unit, stock, available, version) VALUES
                                                                                                  (gen_random_uuid(), 'Calabacín Verde',   (SELECT id FROM varieties WHERE name = 'Verde'    AND product_category = 'Zucchini'), 1.50, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Calabacín Amarillo',(SELECT id FROM varieties WHERE name = 'Amarillo' AND product_category = 'Zucchini'), 1.80, 'EUR', 'KG', 0, false, 0);

-- Lechugas (sin stock)
INSERT INTO products (id, name, variety_id, price, currency, unit, stock, available, version) VALUES
                                                                                                  (gen_random_uuid(), 'Lechuga Romana',        (SELECT id FROM varieties WHERE name = 'Romana'        AND product_category = 'Lettuce'), 1.20, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Lechuga Hoja de Roble', (SELECT id FROM varieties WHERE name = 'Hoja de roble' AND product_category = 'Lettuce'), 1.50, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Lechuga Iceberg',       (SELECT id FROM varieties WHERE name = 'Iceberg'       AND product_category = 'Lettuce'), 1.00, 'EUR', 'KG', 0, false, 0);

-- Cebollas (sin stock)
INSERT INTO products (id, name, variety_id, price, currency, unit, stock, available, version) VALUES
                                                                                                  (gen_random_uuid(), 'Cebolla Blanca', (SELECT id FROM varieties WHERE name = 'Blanca' AND product_category = 'Onion'), 1.20, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Cebolla Morada', (SELECT id FROM varieties WHERE name = 'Morada' AND product_category = 'Onion'), 1.50, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Cebolla Tierna', (SELECT id FROM varieties WHERE name = 'Tierna' AND product_category = 'Onion'), 1.80, 'EUR', 'KG', 0, false, 0);

-- Ajos (sin stock)
INSERT INTO products (id, name, variety_id, price, currency, unit, stock, available, version) VALUES
                                                                                                  (gen_random_uuid(), 'Ajo Blanco', (SELECT id FROM varieties WHERE name = 'Blanco' AND product_category = 'Garlic'), 5.00, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Ajo Morado', (SELECT id FROM varieties WHERE name = 'Morado' AND product_category = 'Garlic'), 6.00, 'EUR', 'KG', 0, false, 0);

-- Pepinos (sin stock)
INSERT INTO products (id, name, variety_id, price, currency, unit, stock, available, version) VALUES
                                                                                                  (gen_random_uuid(), 'Pepino Común', (SELECT id FROM varieties WHERE name = 'Común' AND product_category = 'Cucumber'), 1.20, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Pepino Mini',  (SELECT id FROM varieties WHERE name = 'Mini'  AND product_category = 'Cucumber'), 2.50, 'EUR', 'KG', 0, false, 0);

-- Hierbas aromáticas (sin stock)
INSERT INTO products (id, name, variety_id, price, currency, unit, stock, available, version) VALUES
                                                                                                  (gen_random_uuid(), 'Albahaca Fresca', (SELECT id FROM varieties WHERE name = 'Común'  AND product_category = 'Basil'),    2.00, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Perejil Fresco',  (SELECT id FROM varieties WHERE name = 'Fresco' AND product_category = 'Parsley'),  1.50, 'EUR', 'KG', 0, false, 0),
                                                                                                  (gen_random_uuid(), 'Romero Fresco',   (SELECT id FROM varieties WHERE name = 'Fresco' AND product_category = 'Rosemary'), 2.00, 'EUR', 'KG', 0, false, 0);