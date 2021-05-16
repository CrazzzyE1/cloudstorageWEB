DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    enabled  BOOLEAN DEFAULT TRUE
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

INSERT INTO roles (name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN');

INSERT INTO users (username, password)
VALUES ('login', '$2y$12$TlvcG75oVt/DYhBqIWwVZuSQP.4gKAgLKBLLlfpF5duLPT1cA7qB.'),
       ('log', '$2y$12$TlvcG75oVt/DYhBqIWwVZuSQP.4gKAgLKBLLlfpF5duLPT1cA7qB.'),
       ('log2', '$2y$12$TlvcG75oVt/DYhBqIWwVZuSQP.4gKAgLKBLLlfpF5duLPT1cA7qB.'),
       ('user', '$2y$12$TlvcG75oVt/DYhBqIWwVZuSQP.4gKAgLKBLLlfpF5duLPT1cA7qB.');

INSERT INTO users_roles (user_id, role_id)
VALUES ('1', '1'),
       ('2', '1'),
       ('3', '1'),
       ('4', '2');


INSERT INTO directories (name, user_id)
VALUES ('login', 1),
       ('log', 2),
       ('log2', 3),
       ('user', 4);

INSERT INTO directories (name, user_id, dir_parent_id)
VALUES ('New Folder 11', 1, 1),
       ('New Folder 12', 1, 1),
       ('New Folder 13', 1, 1),
       ('New Folder 15', 1, 4),
       ('New Folder 14', 2, 2);

INSERT INTO files (name, name_system, size, dir_id)
VALUES ('New FILE 11', 'sys_New FILE 11', 1, 1),
       ('New FILE 12', 'sys_New FILE 12', 1, 1),
       ('New FILE 13', 'sys_New FILE 13', 1, 1),
       ('New FILE 14', 'sys_New FILE 14', 1, 4),
       ('New FILE 15', 'sys_New FILE 15', 1, 4),
       ('New FILE 16', 'sys_New FILE 16', 1, 5),
       ('New FILE 17', 'sys_New FILE 16', 1, 7);
