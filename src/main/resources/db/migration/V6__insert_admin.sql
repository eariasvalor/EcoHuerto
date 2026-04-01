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
             '$2a$12$JwTGhAiFKB/IkAVx5TSMVeWfisUG1igC09sVJH.vE1wzqEzvmp3rG',
             'OWNER',
             true,
             NOW(),
             0
         );