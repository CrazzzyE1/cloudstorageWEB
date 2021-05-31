INSERT INTO roles (name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN');

INSERT INTO users (username, password, space)
VALUES ('login', '$2y$12$TlvcG75oVt/DYhBqIWwVZuSQP.4gKAgLKBLLlfpF5duLPT1cA7qB.', 2147483648);

INSERT INTO users_roles (user_id, role_id)
VALUES ('1', '2');

INSERT INTO directories (name, user_id)
VALUES ('login', 1),
       ('login_recycle', 1);