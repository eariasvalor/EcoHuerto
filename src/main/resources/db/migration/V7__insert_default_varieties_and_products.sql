INSERT INTO varieties (id, name, product_category) VALUES
                                                       (gen_random_uuid(), 'Raf',             'Tomato'),
                                                       (gen_random_uuid(), 'Cherry',          'Tomato'),
                                                       (gen_random_uuid(), 'Corazón de buey', 'Tomato'),
                                                       (gen_random_uuid(), 'Mutxamel',        'Tomato'),
                                                       (gen_random_uuid(), 'Rosa de Altea',   'Tomato'),
                                                       (gen_random_uuid(), 'Pera',            'Tomato');

INSERT INTO products (id, name, variety_id, price, currency, unit, stock, available, version)
VALUES
    (gen_random_uuid(), 'Tomate Raf',             (SELECT id FROM varieties WHERE name = 'Raf'),             2.50, 'EUR', 'KG', 100, true, 0),
    (gen_random_uuid(), 'Tomate Cherry',           (SELECT id FROM varieties WHERE name = 'Cherry'),          3.00, 'EUR', 'KG', 80,  true, 0),
    (gen_random_uuid(), 'Tomate Corazón de buey',  (SELECT id FROM varieties WHERE name = 'Corazón de buey'),3.50, 'EUR', 'KG', 60,  true, 0),
    (gen_random_uuid(), 'Tomate Mutxamel',          (SELECT id FROM varieties WHERE name = 'Mutxamel'),        2.80, 'EUR', 'KG', 90,  true, 0),
    (gen_random_uuid(), 'Tomate Rosa de Altea',     (SELECT id FROM varieties WHERE name = 'Rosa de Altea'),  4.00, 'EUR', 'KG', 50,  true, 0),
    (gen_random_uuid(), 'Tomate Pera',              (SELECT id FROM varieties WHERE name = 'Pera'),            2.20, 'EUR', 'KG', 120, true, 0);