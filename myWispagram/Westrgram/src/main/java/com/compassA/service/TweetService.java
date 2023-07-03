/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           ツイートサービス                            */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/17            作成者:山谷開偉             */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
package com.compassA.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.compassA.model.TweetData;
import com.compassA.repository.WispagramRepository;



@Service
public class TweetService {

	//左から順に全てのツイート、ユーザー自身のツイート, いいねしたツイート, ブックマークしたツイート			
	public enum TweetType
	{
		AllTweet,
		OwnTweet, 
		FavTweet, 
		BookMarkTweet	
	}
	private ArrayList<TweetData> tweetList =new ArrayList<TweetData>();//表示するツイートデータのリスト	
	private final int readUnit = 2;	//一度に読み込むツイート数					
	private int	readCount;//現在読み込んだツイートの数					
	private int	currentTweetID;//ツイートの読み取りの際に基準となる、ツイートID					
	private String	searchStatement;//検索文字列					
	//readCount、searchStatementのゲッターとセッターメゾットの制作



	public void	init()
	{//ページの初期化	
		SetNextTweetID();
	}

	public int getReadCount()
	{
		return readCount;
	}
	public ArrayList<TweetData> getTweetList()
	{
		return tweetList;
	}
	public void setTweetList(ArrayList<TweetData> list)
	{
		tweetList = list;
	}
	public void SetNextTweetID()
	{//currentTweetIDを更新。
		//listの最後のTweetリストのidの-1
		//ツイートが0件の場合
		if (tweetList.size() == 0) {
			currentTweetID = 0;
			return;
		}
		int lastIndex = tweetList.size() -1; 
		if(lastIndex == 0)
		{
			currentTweetID = 0;
			return;
		}else
		{
			currentTweetID = tweetList.get(lastIndex).getTweetId() - 1;
		}
	}

	public void setReadCount(int num)
	{
		readCount =num;
	}

	public String getSearchStatement()
	{
		return searchStatement;
	}

	public void setSearchStatement(String str)
	{
		searchStatement =str;
	}

	@Autowired
	WispagramRepository repository;

	//いいねcheck
	public Model favcheck(Model model) {
		List<Boolean> favList = new ArrayList<>();
		for(int i=0; i<tweetList.size(); i++) {
			favList.add(tweetList.get(i).isFavStatus());
		}
		return model.addAttribute("favList",favList );
	}

	public Model favcheck(RedirectAttributes redirectAttributes) {
		List<Boolean> favList = new ArrayList<>();
		for(int i=0; i<tweetList.size(); i++) {
			favList.add(tweetList.get(i).isFavStatus());
		}
		return redirectAttributes.addFlashAttribute("favList",favList );
	}

	// 良いねボタン処理
	public String fav(int listNo) {

		String resultMessage = "";

		TweetData source = tweetList.get(listNo);

		//いいね
		if (!source.isFavStatus()) {
			resultMessage = repository.addFav(LoginUser.selfData.getUserId(), source.getTweetId());
			source.setFavStatus(true);
			source.setFavCount(source.getFavCount() + 1);			
		}
		// いいね取り消し
		else {
			resultMessage = repository.deleteFav(LoginUser.selfData.getUserId(), source.getTweetId());
			source.setFavStatus(false);
			source.setFavCount(source.getFavCount() - 1);
		}

		return resultMessage;
	}

	//ブックマークcheck
	public Model bmcheck(Model model) {
		List<Boolean> bmList = new ArrayList<>();
		for(int i=0; i<tweetList.size(); i++) {
			bmList.add(tweetList.get(i).isBookMarkStatus());
		}
		return model.addAttribute("bmList",bmList );
	}

	public Model bmcheck(RedirectAttributes redirectAttributes) {
		List<Boolean> bmList = new ArrayList<>();
		for(int i=0; i<tweetList.size(); i++) {
			bmList.add(tweetList.get(i).isBookMarkStatus());
		}
		return redirectAttributes.addFlashAttribute("bmList",bmList );
	}

	// 良いねボタン処理
	public String bookMark(int listNo) {

		String resultMessage = "";

		TweetData source = tweetList.get(listNo);

		// ブックマーク
		if (!source.isBookMarkStatus()) {
			resultMessage = repository.addBookMark(LoginUser.selfData.getUserId(), source.getTweetId());
			source.setBookMarkStatus(true);
			source.setBookMarkCount(source.getBookMarkCount() + 1);
		}
		// ブックマーク取り消し
		else {
			resultMessage = repository.deleteBookMark(LoginUser.selfData.getUserId(), source.getTweetId());
			source.setBookMarkStatus(false);
			source.setBookMarkCount(source.getBookMarkCount() - 1);
		}

		return resultMessage;
	}

	//currentTweetIDから読み取る
	public ArrayList<TweetData>	readNextTweet(TweetType type,int userID)
	{
		SetNextTweetID();
		//allTweetだった場合のみ変更
		if(type ==TweetType.AllTweet)
		{
			if(!repository.getTweet(tweetList,currentTweetID,readUnit).equals(WispagramRepository.successMessage))
			{
				//エラー
			}		
		}else {
			if(!repository.getTweet(tweetList,currentTweetID,readUnit, userID,type).equals(WispagramRepository.successMessage))
			{
				//エラー
			}		
		}
		return tweetList;
	}

	//currentTweetIDから読み取る(検索あり)	
	public ArrayList<TweetData>	readNextTweet(TweetType type,String search,int userID)	
	{
		SetNextTweetID();
		setSearchStatement(search);
		//if("Success"==	rs.getTweet(tweetList, currentTweetID,readUnit,search))
		if(type ==TweetType.AllTweet)
		{
			//if(WispagramRepository.successMessage!=rs.getTweet(tweetList,currentTweetID,readUnit,search))
			if(!repository.getTweet(tweetList,currentTweetID,readUnit,search).equals(WispagramRepository.successMessage))

			{
				//エラー
			}		
		}else {
			if(!repository.getTweet(tweetList,currentTweetID,readUnit,search, userID,type).equals(WispagramRepository.successMessage))
			{
				//エラー
			}
		}

		return tweetList;
	}

	//最新のツイートIDを取得してから読みとる
	public ArrayList<TweetData>	updateTweet(TweetType type ,int userID)
	{	
		tweetList.clear();
		if(type ==TweetType.AllTweet)
		{
			if(!repository.getTweet(tweetList,repository.getLatestTweetID(),readUnit).equals(WispagramRepository.successMessage))
			{
				//エラー
			}		
		}else {
			if(!repository.getTweet(tweetList,repository.getLatestTweetID(),readUnit, userID,type) .equals(WispagramRepository.successMessage))
			{
				//エラー
			}
		}
		return tweetList;

	}

	//最新のツイートIDを取得してから読みとる(検索あり)	
	public ArrayList<TweetData>	updateTweet(TweetType type,String search,int userID)
	{	
		tweetList.clear();
		setSearchStatement(search);
		if(type ==TweetType.AllTweet)
		{
			if(!repository.getTweet(tweetList,repository.getLatestTweetID(),readUnit,search).equals(WispagramRepository.successMessage))
			{
				//エラー
			}		
		}else {
			if(!repository.getTweet(tweetList,repository.getLatestTweetID(),readUnit,search, userID,type).equals(WispagramRepository.successMessage))
			{
				//エラー
			}
		}
		return tweetList;
	}

	// 指定数のツイートを読み直す
	public ArrayList<TweetData> reReadTweet(TweetType type ,int userID, int oldLatestId, int tweetCount)
	{
		tweetList.clear();
		if(type ==TweetType.AllTweet)
		{
			if (!repository.getTweet(tweetList, oldLatestId, tweetCount).equals(WispagramRepository.successMessage)) 
			{
			}
		}
		else
		{
			if (!repository.getTweet(tweetList, oldLatestId, tweetCount, userID, type).equals(WispagramRepository.successMessage)) 
			{
			}
		}

		return tweetList;
	}

	// 指定数のツイートを読み直す
	public ArrayList<TweetData> reReadTweet(TweetType type ,int userID, String search, int oldLatestId, int tweetCount)
	{
		tweetList.clear();
		setSearchStatement(search);
		if(type ==TweetType.AllTweet)
		{
			if (!repository.getTweet(tweetList, oldLatestId, tweetCount, search).equals(WispagramRepository.successMessage)) 
			{
			}
		}
		else
		{
			if (!repository.getTweet(tweetList, oldLatestId, tweetCount, search, userID, type).equals(WispagramRepository.successMessage)) 
			{
			}
		}

		return tweetList;
	}

	
	/**
	 * 6/29小島が追加
	 * 一応完成
	 * @param listNo
	 */
	public void sendDeleteTweet(int listNo) {
		TweetData source = tweetList.get(listNo);
		int tweetId = source.getTweetId();
		
		repository.deleteTweet(tweetId);
	}



}





