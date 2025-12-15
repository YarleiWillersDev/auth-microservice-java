INSERT INTO users (name, email, password)
VALUES (
    'Admin',
    'admin@confidence.com',
    '$2a$10$txPNlW5Oo9vpxU/YlaBuSeh6lZXbkNC5OJ8UHKxViT2edD7DVAzR.'
);

INSERT INTO users (name, email, password)
VALUES (
    'User',
    'user@confidence.com',
    '$2a$10$1U0/YT3feHULZPonGs3TW.9kaE6AFaiWWiWOygkuOib1zqP1RWAXi'
);

-- Link admin -> ADMIN role
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@confidence.com'
  AND r.name = 'ADMIN';

-- Link user -> USER role
INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'user@confidence.com'
  AND r.name = 'USER';
