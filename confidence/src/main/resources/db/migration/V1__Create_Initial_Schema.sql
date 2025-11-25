-- Users Table
CREATE TABLE
    users (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        email VARCHAR(255) NOT NULL,
        password VARCHAR(255) NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        UNIQUE (email)
    );

-- Roles Table
CREATE TABLE
    roles (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(255) NOT NULL,
        description VARCHAR(255)
    );

-- User Roles Table
CREATE TABLE
    user_role (
        user_id BIGINT NOT NULL,
        role_id BIGINT NOT NULL,
        PRIMARY KEY (user_id, role_id),
        CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (id),
        CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES roles (id)
    )