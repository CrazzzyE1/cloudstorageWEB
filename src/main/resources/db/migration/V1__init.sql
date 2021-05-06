DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    login    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL,
    removed  SMALLINT            NOT NULL
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
    size        INT,
    date        DATE,
    time        TIME,
    dir_id      INT,
    FOREIGN KEY (dir_id) references directories (id)
);

INSERT INTO users (login, password, removed)
VALUES ('login', 'password', 0),
       ('log', 'pass', 0),
       ('log2', 'pass2', 0);

INSERT INTO directories (name, user_id)
VALUES ('login', 1),
       ('log', 2),
       ('log2', 3);

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
