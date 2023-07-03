/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           検索結果画面                                */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/17            作成者:山本 悠              */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
package com.compassA.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.compassA.model.TweetData;
import com.compassA.service.LoginUser;
import com.compassA.service.TweetService;

@Controller
public class SearchController {
	
	//Autowiredはクラスの呼び出しを簡略化するアノテーション
	ArrayList<TweetData> ListNowAL ; 
	//Model model;
	//2023年4月20日これを弄る事。
	@Autowired
	TweetService Ts;
	
	@Autowired
	ProfileController PC;
	// ページのメタデータ
	private boolean comeBack = false;
	private int readedLatest;
	private int readedTweets = 0;
	private String searchedString;
	
	@PostMapping("/SearchTo_myProfile")
	public String toMyProfile() {
		PC.setUserId(0);
		archivePageMetaData();
		return "redirect:profile";
	}
	
	//クライアントからURLを受け取る
	@GetMapping("/searched")
	public String init(Model model) 
	{
		comeBack = false;
		
		//アカウント登録画面に遷移
		
	     // ユーザーがログインしていない状態であればログイン画面に遷移させる
        // ケース：URL直打ちによる直リンクなど
        if (!LoginUser.isLogin) {
            return "redirect:error";
        }
        updateTweet(model, Ts.getSearchStatement());
        return "searched";
	}
	
	
	//htmlから送られた情報を受け取る
	
	@PostMapping("/searchAdd")
	public String readNextTweet(Model model) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.AllTweet, 0, searchedString, readedLatest, readedTweets);
			comeBack = false;
		}

		//スクロールバーが一番下に行ったらTweet持ってくる
		model.addAttribute("TwList",Ts.readNextTweet(TweetService.TweetType.AllTweet,Ts.getSearchStatement(),0));
		model.addAttribute("search", Ts.getSearchStatement());
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "searched";

	}
	@PostMapping("/searched")
	public String updateTweet(Model model, @RequestParam("search")String search) {

		model.addAttribute("TwList",Ts.updateTweet(TweetService.TweetType.AllTweet,search,0));
		model.addAttribute("search", search);
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "searched";
	}
	
	
	// いいねが押された時のメソッド
		@PostMapping("/SearchFavmark")
		public String AddFavmarkTweet(Model model, @RequestParam("ListNo")int listNo) {
			
			if (comeBack) {
				Ts.reReadTweet(TweetService.TweetType.AllTweet, 0, searchedString, readedLatest, readedTweets);
				comeBack = false;
			}

			// いいね処理
			Ts.fav(listNo);
			
			// 現在表示しているツイートを保持して、ホームに遷移
			model.addAttribute("TwList", Ts.getTweetList());
			model.addAttribute("search", Ts.getSearchStatement());
			model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
			Ts.favcheck(model);
			Ts.bmcheck(model);
			return "searched";
		}
		
		// ブックマークが押された時のメソッド
		@PostMapping("/SearchBookmark")
		public String AddBookmarkTweet(Model model, @RequestParam("ListNo")int listNo) {
			
			if (comeBack) {
				Ts.reReadTweet(TweetService.TweetType.AllTweet, 0, searchedString, readedLatest,readedTweets);
				comeBack = false;
			}

			// ブックマーク処理
			Ts.bookMark(listNo);
			
			// 現在表示しているツイートを保持して、ホームに遷移
			model.addAttribute("TwList", Ts.getTweetList());
			model.addAttribute("search", Ts.getSearchStatement());
			model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
			Ts.favcheck(model);
			Ts.bmcheck(model);
			return "searched";
		}

		// プロフィール画面に遷移するメソッド
		@PostMapping("/SearchTo_user_profile")
		public String toProfile(@RequestParam("ListNo")int listNo) {
			
			if (comeBack) {
				Ts.reReadTweet(TweetService.TweetType.AllTweet, 0, searchedString, readedLatest, readedTweets);
				comeBack = false;
			}
			archivePageMetaData();
			// リスト番号からツイートのユーザーIDを取得
			// ユーザーIDを、ProfileControllerに渡す
			PC.setUserId(Ts.getTweetList().get(listNo).getOwnerId());
			// プロフィール画面に遷移
			return "redirect:profile";
		}

		// このページで読み込んだツイートの情報のメタデータを保存する
		private void archivePageMetaData() {
			comeBack = true;
			readedLatest = Ts.getTweetList().get(0).getTweetId();
			readedTweets = Ts.getTweetList().size();
			searchedString = Ts.getSearchStatement();
		}
}
