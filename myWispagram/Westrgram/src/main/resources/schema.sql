
DROP TABLE IF EXISTS emp;
CREATE TABLE emp
(
    emp_code        INT
  , name            VARCHAR(40)
  , gender          VARCHAR(2)
  , birth_day       DATE
  , blood_type      VARCHAR(2)
  , depart_code     INT
  , job_code        INT
  , boss_code       INT
  , PRIMARY KEY (emp_code)
);

/* ユーザーテーブル */
DROP TABLE IF EXISTS user_data;
CREATE TABLE IF NOT EXISTS user_data (
	user_id 	 	INT 			AUTO_INCREMENT
,	user_number		CHAR(4)			NOT NULL
,	user_name 	 	VARCHAR(30)
,	profile_text 	VARCHAR(200)
,	icon_image	 	VARCHAR(2083)
,	admin_authority BOOLEAN
,	deleted_flg		BOOLEAN
,	PRIMARY KEY(user_id, user_number)
);

/* ログインテーブル */
DROP TABLE IF EXISTS login;
CREATE TABLE IF NOT EXISTS login (
	user_number				CHAR(4)		NOT NULL REFERENCES user_data(user_number) 
		ON DELETE CASCADE ON UPDATE CASCADE
,	password 				VARCHAR(30) NOT NULL
,	sub_password_question 	VARCHAR(30) NOT NULL
,	sub_password_answer 	VARCHAR(30) NOT NULL
);

/* ツイートテーブル */
DROP TABLE IF EXISTS tweet;
CREATE TABLE IF NOT EXISTS tweet (
	tweet_id 		INT 		AUTO_INCREMENT 	PRIMARY KEY
,	owner_user_id 	INT 		NOT NULL 		REFERENCES user_data(user_id)
		ON DELETE CASCADE ON UPDATE CASCADE
,	tweet_text 		VARCHAR(140)
,	image_data1 	VARCHAR(2083)
,	image_data2 	VARCHAR(2083)
,	tweeted_time 	DATETIME 	NOT NULL
);

/* ファボテーブル */
DROP TABLE IF EXISTS favorite;
CREATE TABLE IF NOT EXISTS favorite (
	fav_id 		INT AUTO_INCREMENT PRIMARY KEY
,	user_id 	INT NOT NULL REFERENCES user_data(user_id)
		ON DELETE CASCADE ON UPDATE CASCADE
,	tweet_id 	INT NOT NULL REFERENCES tweet(tweet_id)
		ON DELETE CASCADE ON UPDATE CASCADE
);

/* ブックマークテーブル */
DROP TABLE IF EXISTS book_mark;
CREATE TABLE IF NOT EXISTS book_mark (
	book_mark_id 	INT AUTO_INCREMENT PRIMARY KEY
,	user_id 		INT NOT NULL REFERENCES user_data(user_id)
		ON DELETE CASCADE ON UPDATE CASCADE
,	tweet_id 		INT NOT NULL REFERENCES tweet(tweet_id)
		ON DELETE CASCADE ON UPDATE CASCADE
);

