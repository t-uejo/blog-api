DELETE FROM articles;

ALTER TABLE articles AUTO_INCREMENT = 1;

INSERT INTO articles (title, body)
VALUES ('タイトル1です。', '本文1です。')
      ,('タイトル2です。', '本文2です。')
      ,('タイトル3です。', '本文3です。')
;

DELETE FROM users;

-- password is "password" for all users.
INSERT INTO users (username, password, enabled)
VALUES ('user1', '$2a$10$DRlcbMBDy0/aM73WcZOsOu5S5eNFwKJ84hcQTQf9yYrZ45krVCkBi', true)
      ,('user2', '$2a$10$Eq0KlxCVSIh7RefZNn/Qcum0lXsC11I056T6QOYJFzrKQKYqgXlnu', true)
      ,('user3', '$2a$10$NawWJ5xU2Tr9PaM2KS1U1eW0kfMiFQD5wD6b4Fg0c3Y0iC08AjF8W', true)
;