/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           アカウント登録画面                          */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/21            作成者: 山谷開偉            */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.compassA.model.TweetData;
import com.compassA.repository.WispagramRepository;
import com.compassA.service.LoginUser;
import com.compassA.service.TweetService;

@Controller
public class FavController {
	@Autowired
	public WispagramRepository repository;
	//Autowiredはクラスの呼び出しを簡略化するアノテーション
	ArrayList<TweetData> ListNowAL ; 
	//Model model;
	//2023年4月20日これを弄る事。
	//クライアントからURLを受け取る
	//@GetMapping("/fav")
	public String init(Model model) 
	{
		
		//アカウント登録画面に遷移
		
	     // ユーザーがログインしていない状態であればログイン画面に遷移させる
        // ケース：URL直打ちによる直リンクなど
        if (!LoginUser.isLogin) {
            return "redirect:signin";
        }
        updateTweet(model);
        return "fav";
	}
	
	
	//htmlから送られた情報を受け取る
	@Autowired
	TweetService Ts;
	
	//@PostMapping("/favupdate")
	public String readNextTweet(Model model) {
		
		//スクロールバーが一番下に行ったらTweet持ってくる
		model.addAttribute("TwList",Ts.readNextTweet(TweetService.TweetType.FavTweet,0));
		return "fav";


	}
	public String updateTweet(Model model) {

		model.addAttribute("TwList",Ts.updateTweet(TweetService.TweetType.FavTweet,0));
		return "fav";
	}

	//@PostMapping("/addFavTweet")
	public String addFavTweet(int User,int TwID,int listNo,Model model) {
		//model.addAttribute("TwList",repository.addFav(User,TwID,listNo,model));
		//model.addAttribute("TwList",service.updateTweet(TweetService.TweetType.BookMarkTweet,0));
		return "fav";
	}
	
}
