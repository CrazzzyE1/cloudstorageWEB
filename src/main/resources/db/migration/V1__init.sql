DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    enabled  BOOLEAN DEFAULT TRUE,
    space    BIGINT
);

DROP TABLE IF EXISTS directories;
CREATE TABLE directories
(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    user_id       INT          NOT NULL,
    dir_parent_id INT,
    FOREIGN KEY (user_id) references users (id)
);

DROP TABLE IF EXISTS files;
CREATE TABLE files
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    name_system VARCHAR(255) NOT NULL,
    size        BIGINT,
    strsize     VARCHAR (255),
    date        VARCHAR(255),
    time        VARCHAR(255),
    dir_id      INT,
    FOREIGN KEY (dir_id) references directories (id)
);

DROP TABLE IF EXISTS roles;
CREATE TABLE roles
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

DROP TABLE IF EXISTS users_roles;
CREATE TABLE users_roles
(
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) references users (id),
    FOREIGN KEY (role_id) references roles (id)
);


