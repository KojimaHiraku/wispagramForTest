/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                         リポジトリクラス                              */
/*                                                                       */
/*             SQLを実行し、データベースとやり取りをするクラス           */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/17            作成者:山本 悠              */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
package com.compassA.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.compassA.model.AccountRegistration;
import com.compassA.model.EditableProfileData;
import com.compassA.model.TweetData;
import com.compassA.model.UserData;
import com.compassA.service.LoginUser;
import com.compassA.service.TweetService;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class WispagramRepository {
	// データベースに接続してDMLを実行するクラス
	private final JdbcTemplate jdbcTemplate;
	
	// SQL処理成功時に返す成功文
	public static final String successMessage = "Success";
	
	/* ******************** */
	/* クラスの初期化を行う */
	/* ******************** */
	public void init() {	}
	
	/* ****************************************************************** */
	/* 引数に入力されたデータの整合性をチェックし、登録可能ならtrueを返す */
	/* ****************************************************************** */
	public String checkDataIntegrity(AccountRegistration registarData) {
		
		// データの入力チェック
//		System.out.println("Check AccountRegistration. Enough To Inputs");
		if (registarData.getUserNo().length() != 4)         return "Not IntegrityData : Not 4 Length";
		if (registarData.getUserName().isEmpty())           return "Not IntegrityData : Name is Empty";
		if (registarData.getPassword().isEmpty())           return "Not IntegrityData : Password is Empty";
		if (registarData.getSubPasswordText().isEmpty())    return "Not IntegrityData : SubPasswordText is Empty";
		if (registarData.getSubPasswordAnswer().isEmpty())  return "Not IntegrityData : SubPasswordAnswer is Empty";
		
		// 入力されたユーザー番号を既に使用しているユーザー数を取得する
//		System.out.println("Check AccountRegistration. Duplicate To Input UserNumber");
		String query = "SELECT COUNT(user_number=? OR NULL)"
		             + " AS duplicateUsers FROM user_data";
		
		// 問合せの実行
		Map<String, Object> result = jdbcTemplate.queryForMap(query, registarData.getUserNo());
		
		// ユーザー数が0件なら真を返す
//		System.out.println(result.get("duplicateUsers"));
		if (!((long)result.get("duplicateUsers") == 0)) return "Not IntegrityData : Duplicated Number";
		
		return successMessage;
	}
	
	/* *********************************↓ INSERT ↓********************************* */

	/* **************************************** */
	/* アカウントの新規登録を行い、結果文を返す */
	/* **************************************** */
	public String signUp(AccountRegistration registarData) {
		
		// ユーザー情報の初期値
		String defaultIconImageSource = "/images/default_icon_qryuPvoCgbswnrO4fWcK.png";
		String defaultProfileText     = "はじめまして！！";
		
		// アカウント登録用のSQL文
		String insertUserDataTable = "INSERT INTO user_data (user_number, user_name, profile_text, icon_image, admin_authority, deleted_flg)"
		                           + " VALUES (?, ?, ?, ?, ?, ?)";
		String insertLoginTable    = "INSERT INTO login (user_number, password, sub_password_question, sub_password_answer)"
		                           + " VALUES (?, ?, ?, ?)";
		
		// SQLの結果行数
		int records;
		
		// 登録処理
		try {
			// ユーザーテーブルの新規登録
			records = jdbcTemplate.update(
				insertUserDataTable,             // SQL文
				registarData.getUserNo(),        // ユーザー番号
				registarData.getUserName(),      // ユーザー名
				defaultProfileText,              // プロフィール文
				defaultIconImageSource,          // アイコン画像
				false,                           // 管理者権限
				false                            // 削除済みフラグ
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 追加行が0行ならエラー
		if (records == 0) return "Registar UserRecord Failed";
		
		// 登録処理
		try {
			// ログインテーブルの新規登録
			records = jdbcTemplate.update(
				insertLoginTable,                         // SQL文
				registarData.getUserNo(),                 // ユーザー番号
				registarData.getPassword(),               // パスワード
				registarData.getSubPasswordText(),        // サブパスワード本文
				registarData.getSubPasswordAnswer()       // サブパスワード答え
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 追加行が0行ならエラー
		if (records == 0) return "Registar LoginRecord Failed";
		
		return successMessage;
	}
	
	/* **************** */
	/* ツイート送信処理 */
	/* **************** */
	public String submitTweet(TweetData source) {
		
		// SQL文
		String insertTweet = "INSERT INTO tweet (owner_user_id, tweet_text, image_data1, image_data2, tweeted_time) "
		                   + "VALUES(?, ?, ?, ?, NOW())";
		
		// SQLの結果行数
		int records;
		
		// 送信処理
		try {
			records = jdbcTemplate.update(
				insertTweet,
				source.getOwnerId(),
				source.getTweetText(),
				source.getTweetImage1(),
				source.getTweetImage2()
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 追加行が0行ならエラー
		if (records == 0) return "Submit TweetRecord Failed";
		
		return successMessage;
	}
	
	/* ************** */
	/* いいね送信処理 */
	/* ************** */
	public String addFav(int userId, int tweetId) {
		
		// SQL文
		String insertFav = "INSERT INTO favorite (user_id, tweet_id) "
				+ "SELECT ?, ? WHERE NOT EXISTS ( SELECT * FROM favorite WHERE user_id = ? AND tweet_id = ? )";
		
		// SQLの結果行数
		int records;
		
		// 送信処理
		try {
			records = jdbcTemplate.update(
				insertFav,
				userId,
				tweetId,
				userId,
				tweetId
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 追加行が0行ならエラー
		if (records == 0) return "Registar LoginTable Failed";		
		
		return successMessage;
	}
	
	/* ******************** */
	/* ブックマーク送信処理 */
	/* ******************** */
	public String addBookMark(int userId, int tweetId) {
		
		// SQL文
		String insertBookMark = "INSERT INTO book_mark (user_id, tweet_id) "
				+ "SELECT ?, ? WHERE NOT EXISTS ( SELECT * FROM book_mark WHERE user_id = ? AND tweet_id = ? )";
		
		// SQLの結果行数
		int records;
		
		// 送信処理
		try {
			records = jdbcTemplate.update(
				insertBookMark,
				userId,
				tweetId,
				userId,
				tweetId
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}

		// 追加行が0行ならエラー
		if (records == 0) return "Add BookMarkRecord Failed";		
		
		return successMessage;
	}

	/* *********************************↓ UPDATE ↓********************************* */
	
	public String confirmEdit(EditableProfileData profileData) {
		
		// データが入っているかチェック
		if (profileData == null) return "Can not edit for null data";
		
		// ユーザーID
		int userId = profileData.getUserId();
		
		// SQL文
		String updateLogin    = "UPDATE login SET password = ?, "
			                  + "sub_password_question = ?, sub_password_answer = ? "
			                  + "WHERE user_number = (SELECT user_number FROM user_data WHERE user_id = ?)";
		
		String updateUserData = "UPDATE user_data SET user_name = ?, "
			                  + "profile_text = ?, icon_image = ? "
			                  + "WHERE user_id = ?";
		
		// SQLの結果行数
		int records;
		
		// 送信処理
		try {
			records = jdbcTemplate.update(
				updateLogin,
				profileData.getPassword(),
				profileData.getSubPasswordText(),
				profileData.getSubPasswordAnswer(),
				userId
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 追加行が0行ならエラー
		if (records == 0) return "Edit LoginRecord Failed";
		
		// 送信処理
		try {
			records = jdbcTemplate.update(
				updateUserData,
				profileData.getUserName(),
				profileData.getProfileText(),
				profileData.getIconImage(),
				userId
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 追加行が0行ならエラー
		if (records == 0) return "Edit UserDataRecord Failed";
		
		return successMessage;
	}
	
	/* *********************************↓ DELETE ↓********************************* */
	
	/* ************** */
	/* いいね削除処理 */
	/* ************** */
	public String deleteFav(int userId, int tweetId) {
		
		// SQL文
		String delFav = "DELETE FROM favorite WHERE (user_id = ?) AND (tweet_id = ?)";
		
		// SQLの結果行数
		int records;
		
		// 送信処理
		try {
			records = jdbcTemplate.update(
				delFav,
				userId,
				tweetId
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}

		//削除行が0行ならエラー
		if (records == 0) return "Delete FavoriteRecord Failed";		
		
		return successMessage;
	}

	/* ******************** */
	/* ブックマーク削除処理 */
	/* ******************** */
	public String deleteBookMark(int userId, int tweetId) {
		
		// SQL文
		String delBookMark = "DELETE FROM book_mark WHERE (user_id = ?) AND (tweet_id = ?)";
		
		// SQLの結果行数
		int records;
		
		// 送信処理
		try {
			records = jdbcTemplate.update(
				delBookMark,
				userId,
				tweetId
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		//削除行が0行ならエラー
		if (records == 0) return "Delete BookMarkRecord Failed";		
		
		return successMessage;
	}
	
	/* **************** */
	/* ツイート削除処理 */
	/* **************** */
	public String deleteTweet(int tweetId) {
		
		// SQL文
		String delFav      = "DELETE FROM favorite WHERE tweet_id = ?";
		String delBookMark = "DELETE FROM book_mark WHERE tweet_id = ?";
		String delTweet    = "DELETE FROM tweet WHERE tweet_id = ?";
		
		// SQLの結果行数
		int records;
		
		// 送信処理
		try {
			// いいねテーブルからデータ削除
			jdbcTemplate.update(
				delFav,
				tweetId
			);
			
			// ブックマークテーブルからデータ削除
			jdbcTemplate.update(
				delBookMark,
				tweetId
			);
			
			// ツイートの削除
			records = jdbcTemplate.update(
				delTweet,
				tweetId
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		//削除行が0行ならエラー
		if (records == 0) return "Delete TweetRecord Failed";
		
		return successMessage;
	}
		
	/* ****************** */
	/* アカウント削除処理 */
	/* ****************** */
	public String deleteAccount(int userId) {
		
		// SQL文
		String deleteUser  = "UPDATE user_data SET deleted_flg = true WHERE user_id = ?";
		
		// SQLの結果行数
		int records;
		
		// 送信処理
		try {
			records = jdbcTemplate.update(
				deleteUser,
				userId
			);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		//削除行が0行ならエラー
		if (records == 0) return "Delete UserRecord Failed";		
				
		return successMessage;
	}
	
	/* *********************************↓ SELECT ↓********************************* */
	
	/* **************************************************** */
	/* ユーザー番号からログインチェックに必要なデータを取得 */
	/* **************************************************** */
	public String getAccountData(AccountRegistration outputData, String inputNo) {
		
		// SQL文
		String query = "SELECT user_name, password, sub_password_question, sub_password_answer "
		             + "FROM login JOIN user_data USING(user_number) WHERE user_number = ? AND deleted_flg = FALSE";
		
		// 問合せの実行
		Map<String, Object> result;
		try {
			result = jdbcTemplate.queryForMap(query, inputNo);
		}
		// アカウントが存在せず、問合せが結果を返さなかった場合
		catch (IncorrectResultSizeDataAccessException ex){
			return "not exists account";
		}
		
		// 結果をモデルに挿入
		outputData.setUserNo(inputNo);
		outputData.setUserName(result.get("user_name").toString());
		outputData.setPassword(result.get("password").toString());
		outputData.setSubPasswordText(result.get("sub_password_question").toString());
		outputData.setSubPasswordAnswer(result.get("sub_password_answer").toString());
		
		return successMessage;
	}
	
	/* ******************************************************************************* */
	/* ログイン時LoginUserクラスから呼ばれる想定。ユーザー番号からユーザーデータを返す */
	/* ******************************************************************************* */
	public String login(UserData outputData, String userNo) {
		
		// SQL文
		String query = "SELECT * FROM user_data WHERE user_number = ?";
		
		// 問合せの実行
		Map<String, Object> result;
		try {
			result = jdbcTemplate.queryForMap(query, userNo);
		}
		// アカウントが存在せず、問合せが結果を返さなかった場合
		catch (IncorrectResultSizeDataAccessException ex){
			return "not exists account";
		}
		
		// 結果をモデルに挿入
		outputData.setUserId((int)result.get("user_id"));
		outputData.setUserNo(userNo);
		outputData.setUserName(result.get("user_name").toString());
		outputData.setProfileText(result.get("profile_text").toString());
		outputData.setIconImage(result.get("icon_Image").toString());
		outputData.setAdminAuthority((boolean)result.get("admin_authority"));
		outputData.setDeletedFlg((boolean)result.get("deleted_flg"));
		
		return successMessage;
	}
	
	/* ********************** */
	/* いいねカウント取得処理 */
	/* ********************** */
	public String getFavCount(int tweetId, int listNo, ArrayList<TweetData> tweetList) {
		Map<String, Object> result;
		// SQL文
		String query = "SELECT COUNT(fav_id) AS favCount FROM favorite WHERE favorite.tweet_id = ?";
		// 送信処理
		try {
			result = jdbcTemplate.queryForMap(query, tweetId);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 対象のツイートのいいねの数を設定
		tweetList.get(listNo).setFavCount((int)((long)result.get("favCount")));
		return successMessage;
	}
	
	/* ****************************************** */
	/* 指定したユーザーIDのユーザー情報を取得する */
	/* ****************************************** */
	public String getUserProfile(UserData outputData, int userId) {
		
		// SQL文
		String query = "SELECT * FROM user_data WHERE user_id = ?";
		
		// 問合せの実行
		Map<String, Object> result;
		try {
			result = jdbcTemplate.queryForMap(query, userId);
		}
		// アカウントが存在せず、問合せが結果を返さなかった場合
		catch (IncorrectResultSizeDataAccessException ex){
			return "not exists account";
		}
		
		// 結果をモデルに挿入
		outputData.setUserId(userId);
		outputData.setUserNo(result.get("user_number").toString());
		outputData.setUserName(result.get("user_name").toString());
		outputData.setProfileText(result.get("profile_text").toString());
		outputData.setIconImage(result.get("icon_Image").toString());
		outputData.setAdminAuthority((boolean)result.get("admin_authority"));
		outputData.setDeletedFlg((boolean)result.get("deleted_flg"));
		
		return successMessage;
	}
	
	/* ********************************************************** */
	/* 指定したユーザーIDの編集可能なプロフィールデータを取得する */
	/* ********************************************************** */
	public String getEditableUserProfile(EditableProfileData outputData, int userId) {
		
		// SQL文
		String query = "SELECT user_id, user_name, profile_text, icon_image, "
			         + "password, sub_password_question, sub_password_answer "
			         + "FROM user_data JOIN login USING(user_number) "
			         + "WHERE user_id = ?";
		
		// 問合せの実行
		Map<String, Object> result;
		try {
			result = jdbcTemplate.queryForMap(query, userId);
		}
		// アカウントが存在せず、問合せが結果を返さなかった場合
		catch (IncorrectResultSizeDataAccessException ex){
			return "not exists account";
		}
		
		// 結果をモデルに挿入
		outputData.setUserId(userId);
		outputData.setUserName(result.get("user_name").toString());
		outputData.setProfileText(result.get("profile_text").toString());
		outputData.setIconImage(result.get("icon_image").toString());
		outputData.setPassword(result.get("password").toString());
		outputData.setSubPasswordText(result.get("sub_password_question").toString());
		outputData.setSubPasswordAnswer(result.get("sub_password_answer").toString());
		
		return successMessage;
	}
	
	/* ******************************************************** */
	/* 最新のツイートのツイートIDを取得する。失敗したら-1を返す */
	/* ******************************************************** */
	public int getLatestTweetID() {
		
		// SQL文
		String query = "SELECT tweet_id FROM tweet ORDER BY tweet_id DESC LIMIT 1";
		
		// 問合せの実行
		Map<String, Object> result;
		try {
			result = jdbcTemplate.queryForMap(query);
		}
		// ツイートが存在せず、問合せが結果を返さなかった場合
		catch (IncorrectResultSizeDataAccessException ex){
			return -1;
		}
		
		return (int)result.get("tweet_id");
	}

	/* **************************************************************** */
	/* 最新の検索結果ツイートのツイートIDを取得する。失敗したら-1を返す */
	/* ****************************************************************
	 * 引数1:指定した文字列をツイート文に含むツイートに絞る
	 * */
	public int getLatestTweetID(String serachState) {
		
		// SQL文
		String query = "SELECT tweet_id FROM tweet WHERE tweet_text LIKE '%?%' "
		             + "ORDER BY tweet_id DESC LIMIT 1";
		
		// 問合せの実行
		Map<String, Object> result;
		try {
			result = jdbcTemplate.queryForMap(query, serachState);
		}
		// ツイートが存在せず、問合せが結果を返さなかった場合
		catch (IncorrectResultSizeDataAccessException ex){
			return -1;
		}
		
		return (int)result.get("tweet_id");
	}
	
	/* ************************************************************************ */
	/* 指定したタイプの最新のツイートのツイートIDを取得する。失敗したら-1を返す */
	/* ************************************************************************ 
	 * 引数1:引数2に関連するユーザーID
	 * 引数2:取り出すデータの絞り方
	 * ---- AllTweet     :全ツイートが対象、絞らない
	 * ---- OwnTweet     :引数1のユーザーがツイートしたツイートに絞る
	 * ---- FavTweet     :引数1のユーザーがいいねしたツイートに絞る
	 * ---- BookMarkTweet:引数1のユーザーがブックマークしたツイートに絞る
	 * */
	public int getLatestTweetID(int userId, TweetService.TweetType type) {
		
		// SQL文
		String query = "SELECT tweet_id FROM tweet ";
		
		// 取得したいツイートのタイプによってSQL文の生成を分岐
		switch (type) {
			// 引数なしのメソッドで全体検索
			case AllTweet:
			default:
				return getLatestTweetID();
				
			// 自分がツイートしたツイートのみ
			case OwnTweet:
				query += "WHERE owner_user_id = ? ";
				break;
				
			// いいねテーブルに該当するツイートのみ
			case FavTweet:
				query += "WHERE tweet_id IN (SELECT tweet_id FROM favorite WHERE user_id = ?) ";
				break;
			
			// ブックマークテーブルに該当するツイートのみ
			case BookMarkTweet:
				query += "WHERE tweet_id IN (SELECT tweet_id FROM book_mark WHERE user_id = ?) ";
				break;
		}
		
		// ソートを追加
		query += "ORDER BY tweet_id DESC LIMIT 1";
		
		// 問合せの実行
		Map<String, Object> result;
		try {
			result = jdbcTemplate.queryForMap(query, userId);
		}
		// ツイートが存在せず、問合せが結果を返さなかった場合
		catch (IncorrectResultSizeDataAccessException ex){
			return -1;
		}
		
		return (int)result.get("tweet_id");
	}
	
	/* ******************************************************************************** */
	/* 指定したタイプの最新の検索結果ツイートのツイートIDを取得する。失敗したら-1を返す */
	/* ******************************************************************************** 
	 * 引数1:指定した文字列をツイート文に含むツイートに絞る
	 * 引数2:引数3に関連するユーザーID
	 * 引数3:取り出すデータの絞り方
	 * ---- AllTweet     :全ツイートが対象、文字列の絞りだけ
	 * ---- OwnTweet     :引数2のユーザーがツイートしたツイートに絞る
	 * ---- FavTweet     :引数2のユーザーがいいねしたツイートに絞る
	 * ---- BookMarkTweet:引数2のユーザーがブックマークしたツイートに絞る
	 * */
	public int getLatestTweetID(String searchState, int userId, TweetService.TweetType type) {
		
		// SQL文
		String query = "SELECT tweet_id FROM tweet ";
		
		// 取得したいツイートのタイプによってSQL文の生成を分岐
		switch (type) {
			// 検索のみのメソッドで全体検索
			case AllTweet:
			default:
				return getLatestTweetID(searchState);
				
			// 自分がツイートしたツイートのみ
			case OwnTweet:
				query += "WHERE owner_user_id = ? ";
				break;
				
			// いいねテーブルに該当するツイートのみ
			case FavTweet:
				query += "WHERE tweet_id IN (SELECT tweet_id FROM favorite WHERE user_id = ?) ";
				break;
				
			// ブックマークテーブルに該当するツイートのみ
			case BookMarkTweet:
				query += "WHERE tweet_id IN (SELECT tweet_id FROM book_mark WHERE user_id = ?) ";
				break;
		}
		
		// 検索条件とソートを追加
		query += "AND tweet_text LIKE '%?%' ORDER BY tweet_id DESC LIMIT 1";
		
		// 問合せの実行
		Map<String, Object> result;
		try {
			result = jdbcTemplate.queryForMap(query, userId, searchState);
		}
		// ツイートが存在せず、問合せが結果を返さなかった場合
		catch (IncorrectResultSizeDataAccessException ex){
			return -1;
		}
		
		return (int)result.get("tweet_id");
	}
	
	/* ********************************************************** */
	/* ツイートのIDが大きい順に、引数1番目から引数2分だけ読み取る */
	/* ********************************************************** 
	 * 引数1:ツイートデータのリスト,ここにデータが入る,new だけしておいてね
	 * 引数2:読み込み始めるツイートID
	 * 引数3:読み込む最大ツイート数
	 * */
	public String getTweet(ArrayList<TweetData> outputData, int tweetId, int readUnit) {
		
		// 読み取り始めるツイートのIDは必ず1以上
		if (tweetId < 1) return "No more Tweets";
		
		// 読み込むツイート数も必ず1以上
		if (readUnit < 1) return "Not read at all";
		
		// 自分自身のID
		int ownId = LoginUser.selfData.getUserId();
		
		// SQL文
		String query = "SELECT tweet.tweet_id, tweet.owner_user_id, user_data.user_number, user_data.user_name, user_data.icon_image, "
		             + "(SELECT COUNT(fav_id) FROM favorite WHERE favorite.tweet_id = tweet.tweet_id) AS fav_count, "
		             + "(SELECT COUNT(fav_id) FROM favorite WHERE favorite.tweet_id = tweet.tweet_id AND favorite.user_id = ?) AS fav_status, "
		             + "(SELECT COUNT(book_mark_id) FROM book_mark WHERE book_mark.tweet_id = tweet.tweet_id) AS book_mark_count, "
		             + "(SELECT COUNT(book_mark_id) FROM book_mark WHERE book_mark.tweet_id = tweet.tweet_id AND book_mark.user_id = ?) AS book_mark_status, "
		             + "tweeted_time, tweet_text, image_data1, image_data2 FROM tweet "
		             + "LEFT JOIN user_data ON tweet.owner_user_id = user_data.user_id "
		             + "WHERE tweet.tweet_id BETWEEN 1 AND ? "
		             + "ORDER BY tweet.tweet_id DESC LIMIT ?";
		
		// 問合せの実行
		SqlRowSet result;
		try {
			result = jdbcTemplate.queryForRowSet(query, ownId, ownId, tweetId, readUnit);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 取得したデータをツイートデータリストに分解して追加する
		setTweetDataToList(result, outputData, readUnit);
		
		return successMessage;
	}
	
	/* ************************************************************************************************** */
	/* ツイートのIDが大きい順に、ユーザーIDを元にタイプに合うツイートを、引数1番目から引数2分だけ読み取る */
	/* ************************************************************************************************** 
	 * 引数1:ツイートデータのリスト,ここにデータが入る,new だけしておいてね
	 * 引数2:読み込み始めるツイートID
	 * 引数3:読み込む最大ツイート数
	 * 引数4:引数5に関連するユーザーID
	 * 引数5:取り出すデータの絞り方
	 * ---- AllTweet     :全ツイートが対象、文字列の絞りだけ
	 * ---- OwnTweet     :引数4のユーザーがツイートしたツイートに絞る
	 * ---- FavTweet     :引数4のユーザーがいいねしたツイートに絞る
	 * ---- BookMarkTweet:引数4のユーザーがブックマークしたツイートに絞る
	 * */
	public String getTweet(ArrayList<TweetData> outputData, int tweetId, int readUnit, int userId, TweetService.TweetType type) {
		
		// 読み取り始めるツイートのIDは必ず1以上
		if (tweetId < 1) return "No more Tweets";
		
		// 読み込むツイート数も必ず1以上
		if (readUnit < 1) return "Not read at all";
		
		// 自分自身のID
		int ownId = LoginUser.selfData.getUserId();
		
		// SQL文
		String query = "SELECT tweet.tweet_id, tweet.owner_user_id, user_data.user_number, user_data.user_name, user_data.icon_image, "
		             + "(SELECT COUNT(fav_id) FROM favorite WHERE favorite.tweet_id = tweet.tweet_id) AS fav_count, "
		             + "(SELECT COUNT(fav_id) FROM favorite WHERE favorite.tweet_id = tweet.tweet_id AND favorite.user_id = ?) AS fav_status, "
		             + "(SELECT COUNT(book_mark_id) FROM book_mark WHERE book_mark.tweet_id = tweet.tweet_id) AS book_mark_count, "
		             + "(SELECT COUNT(book_mark_id) FROM book_mark WHERE book_mark.tweet_id = tweet.tweet_id AND book_mark.user_id = ?) AS book_mark_status, "
		             + "tweeted_time, tweet_text, image_data1, image_data2 FROM tweet "
		             + "LEFT JOIN user_data ON tweet.owner_user_id = user_data.user_id "
		             + "WHERE (tweet.tweet_id BETWEEN 1 AND ?) ";
		
		// タイプに応じてWHERE句を修飾
		switch (type) {
			// 修飾なし
			case AllTweet:
			default:
				break;
				
			case OwnTweet:
				query += "AND (tweet.owner_user_id = " + userId + ") ";
				break;
				
			case FavTweet:
				query += "HAVING (fav_status = 1) ";
				break;
				
			case BookMarkTweet:
				query += "HAVING (book_mark_status = 1) ";
				break;
		}
		
		// ツイート順と最大数指定を追加
		query += "ORDER BY tweet.tweet_id DESC LIMIT ?";
		
		// 問合せの実行
		SqlRowSet result;
		try {
			result = jdbcTemplate.queryForRowSet(query, ownId, ownId, tweetId, readUnit);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 取得したデータをツイートデータリストに分解して追加する
		setTweetDataToList(result, outputData, readUnit);
		
		return successMessage;
	}
	
	/* ************************************************************************************ */
	/* ツイートのIDが大きい順に、検索条件に合うツイートを、引数1番目から引数2分だけ読み取る */
	/* ************************************************************************************ 
	 * 引数1:ツイートデータのリスト,ここにデータが入る,new だけしておいてね
	 * 引数2:読み込み始めるツイートID
	 * 引数3:読み込む最大ツイート数
	 * 引数4:指定した文字列をツイート文に含むツイートに絞る
	 * */
	public String getTweet(ArrayList<TweetData> outputData, int tweetId, int readUnit, String searchState) {
		
		// 読み取り始めるツイートのIDは必ず1以上
		if (tweetId < 1) return "No more Tweets";
		
		// 読み込むツイート数も必ず1以上
		if (readUnit < 1) return "Not read at all";
		
		// 自分自身のID
		int ownId = LoginUser.selfData.getUserId();
		
		// SQL文
		String query = "SELECT tweet.tweet_id, tweet.owner_user_id, user_data.user_number, user_data.user_name, user_data.icon_image, "
		             + "(SELECT COUNT(fav_id) FROM favorite WHERE favorite.tweet_id = tweet.tweet_id) AS fav_count, "
		             + "(SELECT COUNT(fav_id) FROM favorite WHERE favorite.tweet_id = tweet.tweet_id AND favorite.user_id = ?) AS fav_status, "
		             + "(SELECT COUNT(book_mark_id) FROM book_mark WHERE book_mark.tweet_id = tweet.tweet_id) AS book_mark_count, "
		             + "(SELECT COUNT(book_mark_id) FROM book_mark WHERE book_mark.tweet_id = tweet.tweet_id AND book_mark.user_id = ?) AS book_mark_status, "
		             + "tweeted_time, tweet_text, image_data1, image_data2 FROM tweet "
		             + "LEFT JOIN user_data ON tweet.owner_user_id = user_data.user_id "
		             + "WHERE (tweet.tweet_id BETWEEN 1 AND ?) AND (tweet_text LIKE ?) "
		             + "ORDER BY tweet.tweet_id DESC LIMIT ?";
		
		// 問合せの実行
		SqlRowSet result;
		try {
			result = jdbcTemplate.queryForRowSet(query, ownId, ownId, tweetId, "%"+searchState+"%", readUnit);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 取得したデータをツイートデータリストに分解して追加する
		setTweetDataToList(result, outputData, readUnit);
		
		return successMessage;
	}
	
	/* ************************************************************************************************************ */
	/* ツイートのIDが大きい順に、ユーザーIDを元にタイプと検索条件に合うツイートを、引数1番目から引数2分だけ読み取る */
	/* ************************************************************************************************************ 
	 * 引数1:ツイートデータのリスト,ここにデータが入る,new だけしておいてね
	 * 引数2:読み込み始めるツイートID
	 * 引数3:読み込む最大ツイート数
	 * 引数4:指定した文字列をツイート文に含むツイートに絞る
	 * 引数5:引数6に関連するユーザーID
	 * 引数6:取り出すデータの絞り方
	 * ---- AllTweet     :全ツイートが対象、文字列の絞りだけ
	 * ---- OwnTweet     :引数5のユーザーがツイートしたツイートに絞る
	 * ---- FavTweet     :引数5のユーザーがいいねしたツイートに絞る
	 * ---- BookMarkTweet:引数5のユーザーがブックマークしたツイートに絞る
	 * */
	public String getTweet(ArrayList<TweetData> outputData, int tweetId, int readUnit, String searchState, int userId, TweetService.TweetType type) {
		
		// 読み取り始めるツイートのIDは必ず1以上
		if (tweetId < 1) return "No more Tweets";
		
		// 読み込むツイート数も必ず1以上
		if (readUnit < 1) return "Not read at all";
		
		// 自分自身のID
		int ownId = LoginUser.selfData.getUserId();
		
		// SQL文
		String query = "SELECT tweet.tweet_id, tweet.owner_user_id, user_data.user_number, user_data.user_name, user_data.icon_image, "
		             + "(SELECT COUNT(fav_id) FROM favorite WHERE favorite.tweet_id = tweet.tweet_id) AS fav_count, "
		             + "(SELECT COUNT(fav_id) FROM favorite WHERE favorite.tweet_id = tweet.tweet_id AND favorite.user_id = ?) AS fav_status, "
		             + "(SELECT COUNT(book_mark_id) FROM book_mark WHERE book_mark.tweet_id = tweet.tweet_id) AS book_mark_count, "
		             + "(SELECT COUNT(book_mark_id) FROM book_mark WHERE book_mark.tweet_id = tweet.tweet_id AND book_mark.user_id = ?) AS book_mark_status, "
		             + "tweeted_time, tweet_text, image_data1, image_data2 FROM tweet "
		             + "LEFT JOIN user_data ON tweet.owner_user_id = user_data.user_id "
		             + "WHERE (tweet.tweet_id BETWEEN 1 AND ?) AND (tweet_text LIKE ?) ";
		
		// タイプに応じてWHERE句を修飾
		switch (type) {
			// 修飾なし
			case AllTweet:
			default:
				break;
				
			case OwnTweet:
				query += "AND (tweet.owner_user_id = " + userId + ") ";
				break;
				
			case FavTweet:
				query += "HAVING (fav_status = 1) ";
				break;
				
			case BookMarkTweet:
				query += "HAVING (book_mark_status = 1) ";
				break;
		}
		
		// ツイート順と最大数指定を追加
		query += "ORDER BY tweet.tweet_id DESC LIMIT ?";
		
		
		// 問合せの実行
		SqlRowSet result;
		try {
			result = jdbcTemplate.queryForRowSet(query, ownId, ownId, tweetId, "%"+searchState+"%", readUnit);
		}
		catch (DataAccessException ex) {
			return "Failed by:" + ex.getMessage();
		}
		
		// 取得したデータをツイートデータリストに分解して追加する
		setTweetDataToList(result, outputData, readUnit);
		
		return successMessage;
	}
	
	/* ********************************************************* */
	/* SqlRowSetに入ったツイートデータを分解し、リストに追加する */
	/* ********************************************************* 
	 * 引数1:データが入ったSqlRowSet
	 * 引数2:データを追加したいツイートデータリスト
	 * 引数3:SqlRowSetの最大行数
	 * */
	private void setTweetDataToList(SqlRowSet result, ArrayList<TweetData> outputData, int readUnit) {
		
		// カーソルのセット
		// 件数が0の場合、後続処理を実行しない
		if(!result.next() && result.getRow() == 0) return;
		
		// 時刻データ変換用クラス
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime time;
		
		// データの分解(readUnitの回数もしくは、行が全て読み終わったら終了)
		for (int i = 0; i < readUnit && !result.isAfterLast(); i++, result.next()) {
			
			// 時刻データを取得
			time = (LocalDateTime)result.getObject("tweeted_time");
			
			// 1行分のデータをTweetDataクラスに挿入
			TweetData tweetData = new TweetData(
				result.getInt("owner_user_id"), 
				result.getString("user_number"), 
				result.getString("user_name"), 
				result.getString("icon_image"), 
				result.getInt("book_mark_count"), 
				result.getInt("book_mark_status") == 1, 
				result.getInt("fav_count"), 
				result.getInt("fav_status") == 1, 
				result.getInt("tweet_id"), 
				time.format(formatter), 
				result.getString("tweet_text"), 
				result.getString("image_data1"), 
				result.getString("image_data2")
			);
			
			// リストにデータを追加
			outputData.add(tweetData);
		}
	}
}


