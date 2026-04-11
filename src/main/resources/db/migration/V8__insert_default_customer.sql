INSERT INTO customers (
    id,
    name,
    email,
    password_hash,
    created_at,
    version
) VALUES (
             gen_random_uuid(),
             'User',
             'user@huerto.com',
             '$2a$12$YNvEZ.X.5MnNX4A.IFq7Delwe1s7XZ.UPfpxDNGqR0i3zF18JyM0C',
             NOW(),
             0
         );