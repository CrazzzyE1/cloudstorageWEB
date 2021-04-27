DROP TABLE IF EXISTS users;
CREATE TABLE users (
                          id SERIAL PRIMARY KEY,
                          login VARCHAR(255) UNIQUE NOT NULL,
                          password VARCHAR(255) NOT NULL,
                          nickname VARCHAR(255) NOT NULL
);

INSERT INTO users (login, password, nickname)
VALUES
('login', 'password', 'nickname'),
('log', 'pass', 'nick'),
('log2', 'pass2', 'nick2');
