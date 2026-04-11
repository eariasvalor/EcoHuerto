INSERT INTO customers (
    id,
    name,
    email,
    password_hash,
    phone_country_code,
    phone_number,
    created_at,
    version
) VALUES (
             gen_random_uuid(),
             'User',
             'user@huerto.com',
             '$2a$12$YNvEZ.X.5MnNX4A.IFq7Delwe1s7XZ.UPfpxDNGqR0i3zF18JyM0C',
             '+34',
             '000000000',
             NOW(),
             0
         );