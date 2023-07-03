/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                      ツイート一つ分のデータ                           */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                2023/04/17            作成者:山本 悠                   */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TweetData {
	// コンストラクタ
	public TweetData(int userId, String text, String image1, String image2){
		ownerId = userId;
		tweetText = text;
		tweetImage1 = image1;
		tweetImage2 = image2;
	}
	public TweetData(){ }

	// ツイートしたユーザーの情報
	private int 	ownerId;
	private String 	ownerNo;
	private String 	ownerName;
	private String 	ownerIconImage;

	// ブックマーク情報
	private int 	bookMarkCount;
	private boolean bookMarkStatus;
	
	// いいね情報
	private int 	favCount;
	private boolean favStatus;
	
	// ツイートの情報
	private int 	tweetId;
	private String 	tweetedTime;
	private String 	tweetText;
	private String 	tweetImage1;
	private String 	tweetImage2;
}

