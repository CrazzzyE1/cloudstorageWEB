INSERT INTO roles (name)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN');

INSERT INTO users (username, password, space)
VALUES ('login', '$2y$12$TlvcG75oVt/DYhBqIWwVZuSQP.4gKAgLKBLLlfpF5duLPT1cA7qB.', 214748364),
       ('admin', '$2y$12$TlvcG75oVt/DYhBqIWwVZuSQP.4gKAgLKBLLlfpF5duLPT1cA7qB.', 214748364);

INSERT INTO users_roles (user_id, role_id)
VALUES ('1', '1'),
       ('2', '2');

INSERT INTO directories (name, user_id)
VALUES ('login', 1),
       ('login_recycle', 1),
       ('admin', 2),
       ('admin_recycle', 2);