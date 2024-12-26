DELETE FROM articles;

ALTER TABLE articles AUTO_INCREMENT = 1;

INSERT INTO articles (title, body)
VALUES ('タイトル1です。', '本文1です。')
      ,('タイトル2です。', '本文2です。')
      ,('タイトル3です。', '本文3です。')
;

DELETE FROM users;

INSERT INTO users (username, password, enabled)
VALUES ('user1', 'password', true)
      ,('user2', 'password', true)
      ,('user3', 'password', true)
;