DELETE FROM roles WHERE name = 'USER';
INSERT INTO roles (name, description) VALUES ('USER', 'Default test user');