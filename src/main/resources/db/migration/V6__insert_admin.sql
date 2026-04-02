INSERT INTO administrators (
    id,
    name,
    email,
    password_hash,
    permission,
    active,
    created_at,
    version
) VALUES (
             gen_random_uuid(),
             'admin',
             'admin@huerto.com',
             '$2a$12$YNvEZ.X.5MnNX4A.IFq7Delwe1s7XZ.UPfpxDNGqR0i3zF18JyM0C',
             'OWNER',
             true,
             NOW(),
             0
         );