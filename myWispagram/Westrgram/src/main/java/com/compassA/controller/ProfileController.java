/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           プロフィール画面                            */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/24            作成者:草木                 */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.compassA.model.UserData;
import com.compassA.repository.WispagramRepository;
import com.compassA.service.LoginUser;
import com.compassA.service.TweetService;

@Controller
public class ProfileController {
	
	// 開いたプロフィールのユーザー情報
	private UserData profileData = new UserData();
	// 開いたプロフィールのユーザーID
	private int	userId;
	// 開いたプロフィールのユーザーが自分かどうか
	private boolean	isSelf;
	
	// ページのメタデータ
	private boolean comeBack = true;
	private int readedLatest = 1;
	private int readedTweets = 0;
	private int readedUserId = 0;

	// リポジトリクラスのインスタンス
	@Autowired
	private WispagramRepository repos;
	// htmlから送られた情報を受け取る
	@Autowired
	TweetService Ts;
	
	
	// ページの初期化
	@GetMapping("/profile")
	public String init(Model model) {
		
		// ユーザーがログインしていない状態であればログイン画面に遷移させる
        // ケース：URL直打ちによる直リンクなど
        if (!LoginUser.isLogin) {
            return "redirect:error";
        }
        
		comeBack = false;        
        // 表示ユーザーが自分かどうか判別
        // 現在表示されているユーザーデータを取得できなかった場合、自分のプロフィール画面を表示
        if (userId == 0) {
        	userId = LoginUser.selfData.getUserId();
        	isSelf = true;
        } else if(userId == LoginUser.selfData.getUserId()) {
        	isSelf = true;
        } else {
        	isSelf = false;
        }
        model.addAttribute("isSelf", isSelf);
        
        // プロフィール情報を取得
        String resultMessage = repos.getUserProfile(profileData, userId);
        // 取得できなかった場合、エラー画面に飛ばす
        if (!resultMessage.equals(WispagramRepository.successMessage)) {
        	return "redirect:error";
        }
        
        // 画面にプロフィール各種情報を受け渡し
        model.addAttribute("user_name", profileData.getUserName());
        model.addAttribute("profile_text", profileData.getProfileText());
        model.addAttribute("icon_image", profileData.getIconImage());
        
        // 最新ツイートを取得、読み込み
        updateTweet(model);
        
        // profile.htmlに遷移
		return "profile";
	}
	
	@PostMapping("/ProfileTo_myProfile")
	public String toMyProfile() {
		archivePageMetaData();
		setUserId(0);
		return "redirect:profile";
	}
	
	// currentTweetIDから読み取る
	@PostMapping("/profileAdd")
	public String readNextTweet(Model model) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.OwnTweet, readedUserId, readedLatest, readedTweets);
			comeBack = false;
		}
		// スクロールバーが一番下に行った場合、Tweetを取得する
		model.addAttribute("TwList",Ts.readNextTweet(TweetService.TweetType.OwnTweet,userId));
		// 画面にプロフィール各種情報を受け渡し
        model.addAttribute("user_name", profileData.getUserName());
        model.addAttribute("profile_text", profileData.getProfileText());
        model.addAttribute("icon_image", profileData.getIconImage());
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
        model.addAttribute("isSelf", isSelf);
		Ts.favcheck(model);
		Ts.bmcheck(model);
        
		return "profile";
	}
	
	// 最新のツイートIDを取得してから読みとる
	public String updateTweet(Model model) {
		
		// プロフィール画面を開いたら、Tweetを取得する
		model.addAttribute("TwList",Ts.updateTweet(TweetService.TweetType.OwnTweet,userId));
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "profile";
	}
	
	// いいねが押された時のメソッド
	@PostMapping("/ProfileFavmark")
	public String AddFavmarkTweet(Model model, @RequestParam("ListNo")int listNo) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.OwnTweet, readedUserId, readedLatest, readedTweets);
			comeBack = false;
		}

		// いいね処理
		Ts.fav(listNo);
		
		// 現在表示しているツイートを保持して、ホームに遷移
		model.addAttribute("TwList", Ts.getTweetList());
		model.addAttribute("user_name", profileData.getUserName());
        model.addAttribute("profile_text", profileData.getProfileText());
        model.addAttribute("icon_image", profileData.getIconImage());
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
        model.addAttribute("isSelf", isSelf);
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "profile";
	}
	
	// ブックマークが押された時のメソッド
	@PostMapping("/ProfileBookmark")
	public String AddBookmarkTweet(Model model, @RequestParam("ListNo")int listNo) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.OwnTweet, readedUserId, readedLatest,readedTweets);
			comeBack = false;
		}

		// ブックマーク処理
		Ts.bookMark(listNo);
		
		// 現在表示しているツイートを保持して、ホームに遷移
		model.addAttribute("TwList", Ts.getTweetList());
        model.addAttribute("user_name", profileData.getUserName());
        model.addAttribute("profile_text", profileData.getProfileText());
        model.addAttribute("icon_image", profileData.getIconImage());
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
        model.addAttribute("isSelf", isSelf);
		Ts.favcheck(model);
		Ts.bmcheck(model);
		return "profile";
	}
	
	// プロフィール画面に遷移するメソッド
	@PostMapping("/ProfileTo_user_profile")
	public String toProfile(@RequestParam("ListNo")int listNo) {
		
		if (comeBack) {
			Ts.reReadTweet(TweetService.TweetType.OwnTweet, readedUserId, readedLatest, readedTweets);
			comeBack = false;
		}
		archivePageMetaData();
		// リスト番号からツイートのユーザーIDを取得
		// ユーザーIDを、ProfileControllerに渡す
		setUserId(Ts.getTweetList().get(listNo).getOwnerId());
		// プロフィール画面に遷移
		return "redirect:profile";
	}

	// このページで読み込んだツイートの情報のメタデータを保存する
	private void archivePageMetaData() {
		comeBack = true;
		readedLatest = Ts.getTweetList().get(0).getTweetId();
		readedTweets = Ts.getTweetList().size();
		readedUserId = userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getUserId() {
		return this.userId;
	}

	
	
	/**
	 * 6/28小島が追加
	 * 一応完成
	 * 他人のツイートも消せてしまう
	 * @param listNo
	 */
	@PostMapping("/ProfileDeleteTweet")
	public String DoDeleteTweet(@RequestParam("ListNo")int listNo) {
		
		Ts.sendDeleteTweet(listNo);
		
		return "redirect:profile";//なんかこの一文を追加したら服部さんが出てこなくなった
	}



}








