/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           ブックマークページのコントローラー                                */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/20            作成者:未定                      */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.compassA.service.LoginUser;
import com.compassA.service.TweetService;

@Controller
public class BookMarkController {
	
	// ページのメタデータ
	private boolean comeBack = false;
	private int readedLatest;
	private int readedTweets = 0;
	
	//クライアントからのページ呼び出し
	@GetMapping("/bookmarks")
	public String init(Model model) {
		comeBack = false;
		//アカウント登録画面に遷移
		
	     // ユーザーがログインしていない状態であればログイン画面に遷移させる
        // ケース：URL直打ちによる直リンクなど
        if (!LoginUser.isLogin) {
            return "redirect:error";
        }
		
      //  LoginUser.selfData;
        
        Ts.favcheck(model);
        Ts.bmcheck(model);
        
		updatebookmarkTweet(model);
		return "bookmarks";
	}
	
	//htmlから送られた情報を受け取る
	//クラスの呼び出しを簡略化するアノテーション
	@Autowired
	TweetService Ts;
	@Autowired
	ProfileController PC;
	
	@PostMapping("/myProfile_bookmarks")
	public String toMyProfile() {
		PC.setUserId(0);
		archivePageMetaData();
		return "redirect:profile";
	}
	
	@PostMapping("/bookmarkAdd")
	public String readNextbookmarkTweet(Model model) {
		
		//スクロールバーが一番下に行ったらTweet持ってくる
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.BookMarkTweet, LoginUser.selfData.getUserId(), readedLatest, readedTweets);
			comeBack = false;
		}
		model.addAttribute("TwList", Ts.readNextTweet(TweetService.TweetType.BookMarkTweet,LoginUser.selfData.getUserId()));
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
		Ts.favcheck(model);
		Ts.bmcheck(model);
		
		return "bookmarks";


	}
	
	@PostMapping("/bookmarkUpdate")
	public String updatebookmarkTweet(Model model) {

		model.addAttribute("TwList", Ts.updateTweet(TweetService.TweetType.BookMarkTweet,LoginUser.selfData.getUserId()));
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());//ログインユーザークラスのセルフデータの中にあるから、それをセットしてやる事。
		Ts.favcheck(model);
		Ts.bmcheck(model);
		
		return "bookmarks";
	}
	
	// いいねが押された時のメソッド
	@PostMapping("/AddFav_bookmarks")
	public String AddFavTweet(Model model, @RequestParam("ListNo")int listNo) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.BookMarkTweet, LoginUser.selfData.getUserId(), readedLatest, readedTweets);
			comeBack = false;
		}

		// いいね処理
		Ts.fav(listNo);
		
		// 現在表示しているツイートを保持して、ホームに遷移
		model.addAttribute("TwList", Ts.getTweetList());
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "bookmarks";
	}
	
	// ブックマークが押された時のメソッド
	@PostMapping("/AddBookmark_bookmarks")
	public String AddBookmarkTweet(Model model, @RequestParam("ListNo")int listNo) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.BookMarkTweet, LoginUser.selfData.getUserId(), readedLatest,readedTweets);
			comeBack = false;
		}

		// ブックマーク処理
		Ts.bookMark(listNo);
		
		// 現在表示しているツイートを保持して、ホームに遷移
		model.addAttribute("TwList", Ts.getTweetList());
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "bookmarks";
	}
	
	// プロフィール画面に遷移するメソッド
	@PostMapping("/user_profile_bookmarks")
	public String toProfile(@RequestParam("ListNo")int listNo) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.BookMarkTweet, LoginUser.selfData.getUserId(), readedLatest, readedTweets);
			comeBack = false;
		}
		// リスト番号からツイートのユーザーIDを取得
		// ユーザーIDを、ProfileControllerに渡す
		PC.setUserId(Ts.getTweetList().get(listNo).getOwnerId());
		// プロフィール画面に遷移
		archivePageMetaData();
		return "redirect:profile";
	}
	
	// このページで読み込んだツイートの情報のメタデータを保存する
	private void archivePageMetaData() {
		comeBack = true;
		readedLatest = Ts.getTweetList().get(0).getTweetId();
		readedTweets = Ts.getTweetList().size();
	}
}
