INSERT INTO emp (emp_code, name, gender, birth_day, blood_type, depart_code, job_code, boss_code) VALUES (101, 'Aoki Shingen',      'M', '1964/09/05', 'A', 2, 1, NULL);
INSERT INTO emp (emp_code, name, gender, birth_day, blood_type, depart_code, job_code, boss_code) VALUES (102, 'Kawamoto Karin',    'F', '1965/01/12', 'O', 1, 1, NULL);
INSERT INTO emp (emp_code, name, gender, birth_day, blood_type, depart_code, job_code, boss_code) VALUES (103, 'Okada Masanobu',    'M', '1979/01/10', 'B', 3, 1, NULL);
INSERT INTO emp (emp_code, name, gender, birth_day, blood_type, depart_code, job_code, boss_code) VALUES (104, 'Bandou Rie',        'F', '1979/07/26', 'O', 1, 2, 102);
INSERT INTO emp (emp_code, name, gender, birth_day, blood_type, depart_code, job_code, boss_code) VALUES (105, 'Adachi Sarasa',     'F', '1979/09/13', 'B', 2, 2, 101);
INSERT INTO emp (emp_code, name, gender, birth_day, blood_type, depart_code, job_code, boss_code) VALUES (106, 'Morishima Harumi',  'F', '1981/02/12', 'AB', 3, 3, 103);
INSERT INTO emp (emp_code, name, gender, birth_day, blood_type, depart_code, job_code, boss_code) VALUES (107, 'Gomi Masayuki',     'M', '1983/06/14', 'A', 3, NULL, 106);
INSERT INTO emp (emp_code, name, gender, birth_day, blood_type, depart_code, job_code, boss_code) VALUES (108, 'Arai Kotomi',       'F', '1985/07/13', 'O', 1, NULL, 104);
INSERT INTO emp (emp_code, name, gender, birth_day, blood_type, depart_code, job_code, boss_code) VALUES (109, 'Morimoto Masaya',   'M', '1995/05/21', 'B', 2, NULL, 105);
INSERT INTO emp (emp_code, name, gender, birth_day, blood_type, depart_code, job_code, boss_code) VALUES (110, 'Huruhashi Akinori', 'M', '1996/01/20', 'O', 3, NULL, 106);

SHOW TABLES;
INSERT INTO login (user_number, password, sub_password_question, sub_password_answer)
VALUES ('0001', '0001', 'What is your name', 'Nobita');

INSERT INTO login (user_number, password, sub_password_question, sub_password_answer)
VALUES ('0002', '0002', 'What is your name', 'Suneo');

INSERT INTO login (user_number, password, sub_password_question, sub_password_answer)
VALUES ('0003', '0003', 'What is your name', 'Giant');


INSERT INTO user_data (user_number, user_name, profile_text, icon_image, admin_authority, deleted_flg)
VALUES ('0001', 'Nobita', 'Doraemoooonn', '/images/default_icon_qryuPvoCgbswnrO4fWcK.png', false, false);

INSERT INTO user_data (user_number, user_name, profile_text, icon_image, admin_authority, deleted_flg)
VALUES ('0002', 'Suneo', 'MammmMaaaaaaaa', '/images/default_icon_qryuPvoCgbswnrO4fWcK.png', false, false);

INSERT INTO user_data (user_number, user_name, profile_text, icon_image, admin_authority, deleted_flg)
VALUES ('0003', 'Giant', 'Voeeeeeeeeeeee', '/images/default_icon_qryuPvoCgbswnrO4fWcK.png', false, false);


INSERT INTO tweet (owner_user_id, tweet_text, image_data1, image_data2, tweeted_time)
VALUES (1, 'Hello!!', NULL, NULL, cast(now() as datetime));

INSERT INTO tweet (owner_user_id, tweet_text, image_data1, image_data2, tweeted_time)
VALUES (1, 'World!!', NULL, NULL, cast(now() as datetime));

INSERT INTO tweet (owner_user_id, tweet_text, image_data1, image_data2, tweeted_time)
VALUES (2, 'Serious?', NULL, NULL, cast(now() as datetime));

INSERT INTO tweet (owner_user_id, tweet_text, image_data1, image_data2, tweeted_time)
VALUES (2, 'is Started?', NULL, NULL, cast(now() as datetime));

INSERT INTO tweet (owner_user_id, tweet_text, image_data1, image_data2, tweeted_time)
VALUES (3, 'Im Here!', NULL, NULL, cast(now() as datetime));


INSERT INTO favorite (user_id, tweet_id)
VALUES (3, 1);

INSERT INTO favorite (user_id, tweet_id)
VALUES (3, 2);

INSERT INTO favorite (user_id, tweet_id)
VALUES (1, 5);

INSERT INTO favorite (user_id, tweet_id)
VALUES (1, 4);


INSERT INTO book_mark (user_id, tweet_id)
VALUES (3, 1);

INSERT INTO book_mark (user_id, tweet_id)
VALUES (1, 3);


