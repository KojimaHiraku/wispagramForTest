/* 「サンプル」データベースを作成 */
CREATE DATABASE サンプル;

/* 「サンプル」データベースを選択 */
USE サンプル;

/* 「メニュー」テーブル */
CREATE TABLE メニュー
(
    メニュー名      VARCHAR(20)
  , 値段            INT
  , 残数            INT
);
INSERT INTO メニュー (メニュー名, 値段, 残数) VALUES ('ハンバーグ', 600, 10);
INSERT INTO メニュー (メニュー名, 値段, 残数) VALUES ('オムライス', 800, 8);
INSERT INTO メニュー (メニュー名, 値段, 残数) VALUES ('スープ', 300, 15);

/* 「売上」テーブル */
CREATE TABLE 売上
(
    日付            DATE
  , メニュー名      VARCHAR(100)
  , 数量            INT
  , 売上金額        INT
);
INSERT INTO 売上 (日付, メニュー名, 数量, 売上金額) VALUES ('2021/03/28', 'スープ', 3, 900);
INSERT INTO 売上 (日付, メニュー名, 数量, 売上金額) VALUES ('2021/03/29', 'オムライス', 2, 1600);
INSERT INTO 売上 (日付, メニュー名, 数量, 売上金額) VALUES ('2021/03/30', 'スープ', 2, 600);

/* 「家計簿」テーブル */
CREATE TABLE 家計簿
(
    No              INT
  , 日付            DATE
  , 項目            VARCHAR(100)
  , 品名            VARCHAR(100)
  , 金額            INT
  , PRIMARY KEY (No)
);
INSERT INTO 家計簿 (No, 日付, 項目, 品名, 金額) VALUES (1, '2021/03/27', '食費', '大根', 100);
INSERT INTO 家計簿 (No, 日付, 項目, 品名, 金額) VALUES (2, '2021/03/27', '食費', '豚バラ肉', 300);
INSERT INTO 家計簿 (No, 日付, 項目, 品名, 金額) VALUES (3, '2021/03/27', '日用品', 'ティッシュ', 230);
INSERT INTO 家計簿 (No, 日付, 項目, 品名, 金額) VALUES (4, '2021/03/28', '娯楽費', '雑誌', 700);
INSERT INTO 家計簿 (No, 日付, 項目, 品名, 金額) VALUES (5, '2021/03/28', 'おやつ', 'ドーナツ', 120);

/* 「部門」テーブル */
CREATE TABLE 部門
(
    部門コード      INT
  , 部門名          VARCHAR(40)
  , PRIMARY KEY (部門コード)
);
INSERT INTO 部門 (部門コード, 部門名) VALUES (1, '総務');
INSERT INTO 部門 (部門コード, 部門名) VALUES (2, '営業');
INSERT INTO 部門 (部門コード, 部門名) VALUES (3, '開発');

/* 「役職」テーブル */
CREATE TABLE 役職
(
    役職コード      INT
  , 役職名          VARCHAR(40)
  , PRIMARY KEY (役職コード)
);
INSERT INTO 役職 (役職コード, 役職名) VALUES (1, '部長');
INSERT INTO 役職 (役職コード, 役職名) VALUES (2, '課長');
INSERT INTO 役職 (役職コード, 役職名) VALUES (3, '係長');

/* 「社員」テーブル */
CREATE TABLE 社員
(
    社員コード      INT
  , 社員名          VARCHAR(40)
  , 性別            VARCHAR(2)
  , 生年月日        DATE
  , 血液型          VARCHAR(2)
  , 部門コード      INT
  , 役職コード      INT
  , 上司社員コード  INT
  , PRIMARY KEY (社員コード)
  , FOREIGN KEY (部門コード) REFERENCES 部門(部門コード)
  , FOREIGN KEY (役職コード) REFERENCES 役職(役職コード)
  , FOREIGN KEY (上司社員コード) REFERENCES 社員(社員コード)
);
INSERT INTO 社員 (社員コード, 社員名, 性別, 生年月日, 血液型, 部門コード, 役職コード, 上司社員コード) VALUES (101, '青木　信玄', '男', '1964/09/05', 'A', 2, 1, NULL);
INSERT INTO 社員 (社員コード, 社員名, 性別, 生年月日, 血液型, 部門コード, 役職コード, 上司社員コード) VALUES (102, '川本　夏鈴', '女', '1965/01/12', 'O', 1, 1, NULL);
INSERT INTO 社員 (社員コード, 社員名, 性別, 生年月日, 血液型, 部門コード, 役職コード, 上司社員コード) VALUES (103, '岡田　雅宣', '男', '1979/01/10', 'B', 3, 1, NULL);
INSERT INTO 社員 (社員コード, 社員名, 性別, 生年月日, 血液型, 部門コード, 役職コード, 上司社員コード) VALUES (104, '坂東　理恵', '女', '1979/07/26', 'O', 1, 2, 102);
INSERT INTO 社員 (社員コード, 社員名, 性別, 生年月日, 血液型, 部門コード, 役職コード, 上司社員コード) VALUES (105, '安達　更紗', '女', '1979/09/13', 'B', 2, 2, 101);
INSERT INTO 社員 (社員コード, 社員名, 性別, 生年月日, 血液型, 部門コード, 役職コード, 上司社員コード) VALUES (106, '森島　春美', '女', '1981/02/12', 'AB', 3, 3, 103);
INSERT INTO 社員 (社員コード, 社員名, 性別, 生年月日, 血液型, 部門コード, 役職コード, 上司社員コード) VALUES (107, '五味　昌幸', '男', '1983/06/14', 'A', 3, NULL, 106);
INSERT INTO 社員 (社員コード, 社員名, 性別, 生年月日, 血液型, 部門コード, 役職コード, 上司社員コード) VALUES (108, '新井　琴美', '女', '1985/07/13', 'O', 1, NULL, 104);
INSERT INTO 社員 (社員コード, 社員名, 性別, 生年月日, 血液型, 部門コード, 役職コード, 上司社員コード) VALUES (109, '森本　昌也', '男', '1995/05/21', 'B', 2, NULL, 105);
INSERT INTO 社員 (社員コード, 社員名, 性別, 生年月日, 血液型, 部門コード, 役職コード, 上司社員コード) VALUES (110, '古橋　明憲', '男', '1996/01/20', 'O', 3, NULL, 106);

/* 「給与」テーブル */
CREATE TABLE 給与
(
    社員コード      INT
  , 金額            INT
  , FOREIGN KEY (社員コード) REFERENCES 社員(社員コード)
);
INSERT INTO 給与 (社員コード, 金額) VALUES (101, 1000000);
INSERT INTO 給与 (社員コード, 金額) VALUES (102, 952000);
INSERT INTO 給与 (社員コード, 金額) VALUES (103, 702000);
INSERT INTO 給与 (社員コード, 金額) VALUES (104, 640000);
INSERT INTO 給与 (社員コード, 金額) VALUES (105, 636000);
INSERT INTO 給与 (社員コード, 金額) VALUES (106, 591000);
INSERT INTO 給与 (社員コード, 金額) VALUES (107, 404000);
INSERT INTO 給与 (社員コード, 金額) VALUES (108, 388000);
INSERT INTO 給与 (社員コード, 金額) VALUES (109, 307000);
INSERT INTO 給与 (社員コード, 金額) VALUES (110, 287000);

/* 「システム利用時間」テーブル */
CREATE TABLE システム利用時間
(
    社員コード      INT
  , 日付            DATE
  , 秒数            INT
  , FOREIGN KEY (社員コード) REFERENCES 社員(社員コード)
);
INSERT INTO システム利用時間 (社員コード, 日付, 秒数) VALUES (101, '2021/08/01', 2498);
INSERT INTO システム利用時間 (社員コード, 日付, 秒数) VALUES (102, '2021/08/01', 1175);
INSERT INTO システム利用時間 (社員コード, 日付, 秒数) VALUES (103, '2021/08/01', 2108);
INSERT INTO システム利用時間 (社員コード, 日付, 秒数) VALUES (104, '2021/08/01', 3263);
INSERT INTO システム利用時間 (社員コード, 日付, 秒数) VALUES (105, '2021/08/01', 2808);
INSERT INTO システム利用時間 (社員コード, 日付, 秒数) VALUES (106, '2021/08/01', 2543);
INSERT INTO システム利用時間 (社員コード, 日付, 秒数) VALUES (107, '2021/08/01', 3219);
INSERT INTO システム利用時間 (社員コード, 日付, 秒数) VALUES (108, '2021/08/01', 1532);
INSERT INTO システム利用時間 (社員コード, 日付, 秒数) VALUES (109, '2021/08/01', 3510);
INSERT INTO システム利用時間 (社員コード, 日付, 秒数) VALUES (110, '2021/08/01', 2928);

/* 「業種」テーブル */
CREATE TABLE 業種
(
    業種コード      INT
  , 業種名          VARCHAR(20)
  , PRIMARY KEY (業種コード)
);
INSERT INTO 業種 (業種コード, 業種名) VALUES (1, '製造業');
INSERT INTO 業種 (業種コード, 業種名) VALUES (2, '観光業');
INSERT INTO 業種 (業種コード, 業種名) VALUES (3, '情報通信業');
INSERT INTO 業種 (業種コード, 業種名) VALUES (4, '小売業');

/* 「取引先」テーブル */
CREATE TABLE 取引先
(
    取引先コード    INT
  , 取引先名        VARCHAR(40)
  , 業種コード      VARCHAR(20)
  , PRIMARY KEY (取引先コード )
);
INSERT INTO 取引先 (取引先コード, 取引先名, 業種コード) VALUES (1, '北海道製作所', 1);
INSERT INTO 取引先 (取引先コード, 取引先名, 業種コード) VALUES (2, '青森観光', 2);
INSERT INTO 取引先 (取引先コード, 取引先名, 業種コード) VALUES (3, '岩手通信', 3);
INSERT INTO 取引先 (取引先コード, 取引先名, 業種コード) VALUES (4, '宮城開発', 3);
INSERT INTO 取引先 (取引先コード, 取引先名, 業種コード) VALUES (5, '秋田商事', 5);
INSERT INTO 取引先 (取引先コード, 取引先名, 業種コード) VALUES (6, '山形電機', NULL);
