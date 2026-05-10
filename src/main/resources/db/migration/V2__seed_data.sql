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
INSERT INTO products (id, name, variety_id, description, price, currency, unit, stock, available, version) VALUES
                                                                                                               (gen_random_uuid(), 'Tomate Raf',            (SELECT id FROM varieties WHERE name = 'Raf'             AND product_category = 'Tomato'), 'Tomate premium de la huerta, sabor intenso y carne firme. Ideal para ensaladas y consumo en crudo.',                          2.50, 'EUR', 'KG', 100, true,  0),
                                                                                                               (gen_random_uuid(), 'Tomate Cherry',          (SELECT id FROM varieties WHERE name = 'Cherry'          AND product_category = 'Tomato'), 'Pequeño y dulce, perfecto para ensaladas, tapas y guarniciones. Recolectado en su punto óptimo de maduración.',              3.00, 'EUR', 'KG', 80,  true,  0),
                                                                                                               (gen_random_uuid(), 'Tomate Corazón de buey', (SELECT id FROM varieties WHERE name = 'Corazón de buey' AND product_category = 'Tomato'), 'Tomate de gran tamaño con forma característica, muy carnoso y con pocas semillas. Excelente para bocadillos y ensaladas.',   3.50, 'EUR', 'KG', 60,  true,  0),
                                                                                                               (gen_random_uuid(), 'Tomate Mutxamel',        (SELECT id FROM varieties WHERE name = 'Mutxamel'        AND product_category = 'Tomato'), 'Variedad tradicional valenciana de sabor dulce y equilibrado. Muy apreciado en la cocina mediterránea.',                      2.80, 'EUR', 'KG', 90,  true,  0),
                                                                                                               (gen_random_uuid(), 'Tomate Rosa de Altea',   (SELECT id FROM varieties WHERE name = 'Rosa de Altea'   AND product_category = 'Tomato'), 'Tomate rosado de alta gama, jugoso y con un sabor excepcional. Uno de los más valorados de la Marina Alta.',                 4.00, 'EUR', 'KG', 50,  true,  0),
                                                                                                               (gen_random_uuid(), 'Tomate Pera',            (SELECT id FROM varieties WHERE name = 'Pera'            AND product_category = 'Tomato'), 'Forma alargada y carne compacta, ideal para salsas, sofritos y conservas caseras. Bajo nivel de acidez.',                    2.20, 'EUR', 'KG', 120, true,  0);

-- Pimientos (sin stock)
INSERT INTO products (id, name, variety_id, description, price, currency, unit, stock, available, version) VALUES
                                                                                                               (gen_random_uuid(), 'Pimiento Morrón Rojo',  (SELECT id FROM varieties WHERE name = 'Morrón rojo'  AND product_category = 'Pepper'), 'Pimiento rojo maduro, dulce y carnoso. Perfecto asado, en ensaladas o salteado con aceite de oliva.',        2.20, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Pimiento Morrón Verde', (SELECT id FROM varieties WHERE name = 'Morrón verde' AND product_category = 'Pepper'), 'Pimiento verde fresco con un toque ligeramente amargo. Muy versátil en la cocina, crudo o cocinado.',         1.80, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Pimiento Italiano',     (SELECT id FROM varieties WHERE name = 'Italiano'     AND product_category = 'Pepper'), 'Pimiento largo y fino, suave y dulce. Ideal para freír o asar a la plancha con un poco de sal gruesa.',       2.00, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Pimiento de Padrón',    (SELECT id FROM varieties WHERE name = 'Padrón'       AND product_category = 'Pepper'), 'Pequeños pimientos verdes para freír en aceite de oliva. Unos pican y otros no, ¡esa es su gracia!',         3.50, 'EUR', 'KG', 0, false, 0);

-- Berenjenas (sin stock)
INSERT INTO products (id, name, variety_id, description, price, currency, unit, stock, available, version) VALUES
                                                                                                               (gen_random_uuid(), 'Berenjena Común',  (SELECT id FROM varieties WHERE name = 'Común'  AND product_category = 'Eggplant'), 'Berenjena clásica de piel morada oscura y carne blanca. Perfecta para la escalivada, berenjenas rellenas o al horno.',  1.80, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Berenjena Rayada', (SELECT id FROM varieties WHERE name = 'Rayada' AND product_category = 'Eggplant'), 'Variedad de rayas violetas y blancas, más dulce y tierna que la común. Excelente a la plancha o en frituras.',          2.20, 'EUR', 'KG', 0, false, 0);

-- Calabacines (sin stock)
INSERT INTO products (id, name, variety_id, description, price, currency, unit, stock, available, version) VALUES
                                                                                                               (gen_random_uuid(), 'Calabacín Verde',    (SELECT id FROM varieties WHERE name = 'Verde'    AND product_category = 'Zucchini'), 'Calabacín verde clásico, tierno y versátil. Ideal a la plancha, en crema, relleno o en espaguetis de verdura.',    1.50, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Calabacín Amarillo', (SELECT id FROM varieties WHERE name = 'Amarillo' AND product_category = 'Zucchini'), 'Variedad amarilla de sabor más suave y dulce. Aporta un toque de color y distinción a cualquier plato.',           1.80, 'EUR', 'KG', 0, false, 0);

-- Lechugas (sin stock)
INSERT INTO products (id, name, variety_id, description, price, currency, unit, stock, available, version) VALUES
                                                                                                               (gen_random_uuid(), 'Lechuga Romana',        (SELECT id FROM varieties WHERE name = 'Romana'        AND product_category = 'Lettuce'), 'Hojas alargadas y crujientes, base de la clásica ensalada César. Aguanta bien el aliño sin perder textura.',     1.20, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Lechuga Hoja de Roble', (SELECT id FROM varieties WHERE name = 'Hoja de roble' AND product_category = 'Lettuce'), 'Hoja ondulada de color rojizo y sabor suave. Muy decorativa y tierna, perfecta para ensaladas mixtas.',         1.50, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Lechuga Iceberg',       (SELECT id FROM varieties WHERE name = 'Iceberg'       AND product_category = 'Lettuce'), 'Cogollo compacto y muy crujiente. Sabor neutro y refrescante, ideal para hamburguesas y ensaladas ligeras.',    1.00, 'EUR', 'KG', 0, false, 0);

-- Cebollas (sin stock)
INSERT INTO products (id, name, variety_id, description, price, currency, unit, stock, available, version) VALUES
                                                                                                               (gen_random_uuid(), 'Cebolla Blanca', (SELECT id FROM varieties WHERE name = 'Blanca' AND product_category = 'Onion'), 'Sabor suave y ligeramente dulce. Perfecta para sofitos, caldos y guisos. Base de la cocina mediterránea.',             1.20, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Cebolla Morada', (SELECT id FROM varieties WHERE name = 'Morada' AND product_category = 'Onion'), 'Color intenso y sabor más dulce que la blanca. Ideal en crudo para ensaladas, ceviches y encurtidos.',                1.50, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Cebolla Tierna', (SELECT id FROM varieties WHERE name = 'Tierna' AND product_category = 'Onion'), 'Cebolleta fresca de temporada, suave y aromática. Perfecta para calçots, a la plancha o picada en ensaladas.',        1.80, 'EUR', 'KG', 0, false, 0);

-- Ajos (sin stock)
INSERT INTO products (id, name, variety_id, description, price, currency, unit, stock, available, version) VALUES
                                                                                                               (gen_random_uuid(), 'Ajo Blanco', (SELECT id FROM varieties WHERE name = 'Blanco' AND product_category = 'Garlic'), 'Ajo de sabor intenso y persistente. Imprescindible en la cocina española para sofritos, aliolis y marinados.',       5.00, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Ajo Morado', (SELECT id FROM varieties WHERE name = 'Morado' AND product_category = 'Garlic'), 'Variedad más suave y aromática que el blanco. Muy apreciada por chefs por su sabor fino y menos picante.',            6.00, 'EUR', 'KG', 0, false, 0);

-- Pepinos (sin stock)
INSERT INTO products (id, name, variety_id, description, price, currency, unit, stock, available, version) VALUES
                                                                                                               (gen_random_uuid(), 'Pepino Común', (SELECT id FROM varieties WHERE name = 'Común' AND product_category = 'Cucumber'), 'Pepino fresco y crujiente, bajo en calorías. Ideal en ensaladas, gazpacho y como snack saludable.',                1.20, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Pepino Mini',  (SELECT id FROM varieties WHERE name = 'Mini'  AND product_category = 'Cucumber'), 'Versión pequeña y sin semillas, piel fina comestible. Perfecto para picar, ensaladas y como acompañamiento.',      2.50, 'EUR', 'KG', 0, false, 0);

-- Hierbas aromáticas (sin stock)
INSERT INTO products (id, name, variety_id, description, price, currency, unit, stock, available, version) VALUES
                                                                                                               (gen_random_uuid(), 'Albahaca Fresca', (SELECT id FROM varieties WHERE name = 'Común'  AND product_category = 'Basil'),    'Hierba aromática de aroma intenso. Imprescindible para el pesto, ensalada caprese y pastas italianas.',           2.00, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Perejil Fresco',  (SELECT id FROM varieties WHERE name = 'Fresco' AND product_category = 'Parsley'),  'Hierba fresca de uso universal en la cocina española. Ideal para marinados, picadas, arroces y mariscos.',      1.50, 'EUR', 'KG', 0, false, 0),
                                                                                                               (gen_random_uuid(), 'Romero Fresco',   (SELECT id FROM varieties WHERE name = 'Fresco' AND product_category = 'Rosemary'), 'Aromático y resinoso, perfecto para carnes asadas, patatas al horno y aceites aromatizados.',                   2.00, 'EUR', 'KG', 0, false, 0);