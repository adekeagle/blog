INSERT INTO roles VALUES(null, 'ROLE_ADMIN');
INSERT INTO roles VALUES(null, 'ROLE_USER');

INSERT INTO users values(null, 'admin@admin.pl', 'admin', '$2a$10$YX4ZngPHe2bcj0/kME9GxuzSSex//2rfGlWVWBjSqsLMtWJ9q331C', 'admin');
INSERT INTO users values(null, 'test@test.pl', 'test', '$2a$10$YX4ZngPHe2bcj0/kME9GxuzSSex//2rfGlWVWBjSqsLMtWJ9q331C', 'test');

INSERT INTO users_roles VALUES(1, 1);
INSERT INTO users_roles VALUES(2, 1);