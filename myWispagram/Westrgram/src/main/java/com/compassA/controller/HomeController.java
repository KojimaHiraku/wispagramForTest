/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           アカウント登録画面                          */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/19            作成者: 山谷開偉            */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.compassA.repository.WispagramRepository;
import com.compassA.service.LoginUser;
import com.compassA.service.TweetService;

@Controller
public class HomeController {
	
	// ページのメタデータ
	private boolean comeBack = false;
	private int readedLatest;
	private int readedTweets = 0;
	
	//Autowiredはクラスの呼び出しを簡略化するアノテーション
	//Model model;
	//2023年4月20日これを弄る事。
	//クライアントからURLを受け取る
	@GetMapping("/home")
	public String init(Model model) 
	{
		comeBack = false;
		//アカウント登録画面に遷移
		
	     // ユーザーがログインしていない状態であればログイン画面に遷移させる
        // ケース：URL直打ちによる直リンクなど
        if (!LoginUser.isLogin) {
            return "redirect:signin";
        }
        updateTweet(model);
      //  LoginUser.selfData;
        
        Ts.favcheck(model);
        Ts.bmcheck(model);
        
        return "home";
	}
	
	//htmlから送られた情報を受け取る
	@Autowired
	TweetService Ts;
	@Autowired
	ProfileController PC;
	
	@Autowired
	WispagramRepository repos;
	
	@PostMapping("/myProfile")
	public String toMyProfile() {
		PC.setUserId(0);
		archivePageMetaData();
		return "redirect:profile";
	}
	
	@PostMapping("/homeAdd")
	public String readNextTweet(Model model) {
		
		//スクロールバーが一番下に行ったらTweet持ってくる
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.AllTweet, 0, readedLatest, readedTweets);
			comeBack = false;
		}
		model.addAttribute("TwList", Ts.readNextTweet(TweetService.TweetType.AllTweet,0));
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "home";
	}
	
	@PostMapping("/homeUpdate")
	public String updateTweet(Model model) {
		
		model.addAttribute("TwList", Ts.updateTweet(TweetService.TweetType.AllTweet,0));
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());//ログインユーザークラスのセルフデータの中にあるから、それをセットしてやる事。
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "home";
	}
	
	// いいねが押された時のメソッド
	@PostMapping("/HomeFavmark")
	public String AddFavmarkTweet(Model model, @RequestParam("ListNo")int listNo) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.AllTweet, 0, readedLatest, readedTweets);
			comeBack = false;
		}

		// いいね処理
		Ts.fav(listNo);
		
		// 現在表示しているツイートを保持して、ホームに遷移
		model.addAttribute("TwList", Ts.getTweetList());
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "home";
	}
	
	// ブックマークが押された時のメソッド
	@PostMapping("/HomeBookmark")
	public String AddBookmarkTweet(Model model, @RequestParam("ListNo")int listNo) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.AllTweet, 0, readedLatest,readedTweets);
			comeBack = false;
		}

		// ブックマーク処理
		Ts.bookMark(listNo);
		
		// 現在表示しているツイートを保持して、ホームに遷移
		model.addAttribute("TwList", Ts.getTweetList());
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "home";
	}
	
	// プロフィール画面に遷移するメソッド
	@PostMapping("/user_profile")
	public String toProfile(@RequestParam("ListNo")int listNo) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.AllTweet, 0, readedLatest, readedTweets);
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
	}
}
