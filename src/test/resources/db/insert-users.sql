-- Insert test user (username: admin, password: password)
INSERT INTO `users` (`username`, `password`, `enabled`)
VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 1);

INSERT INTO `authorities` (`username`, `authority`)
VALUES ('admin', 'ROLE_USER'),
       ('admin', 'ROLE_ADMIN');