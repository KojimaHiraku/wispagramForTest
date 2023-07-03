/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                      ログイン・サブログイン画面                       */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/19            作成者:草木                 */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.compassA.model.AccountRegistration;
import com.compassA.repository.WispagramRepository;
import com.compassA.service.LoginUser;

@Controller
public class LoginController {
	private AccountRegistration inquiryData = new AccountRegistration();
	
	// Autowire（クラス呼び出し簡略化）
	@Autowired
	private WispagramRepository repos;
	
	/* メインログイン */
	
	// メインログイン、ページの初期化
	@GetMapping("/signin")
	public String init1() {
		// ログイン状態を初期化
		LoginUser.isLogin=false;
		
		// signin.htmlに画面遷移
		return "signin";
	}
	
	// アカウントデータを送信してログインし、ログインユーザーデータをセット
	// メインログイン画面
	@PostMapping("/signin")
	public String mainlogin(@RequestParam("ID")String inputNo,
				@RequestParam("Password")String inputPassword,
				Model model,
				RedirectAttributes redirectAttributes) {
		
		redirectAttributes.addFlashAttribute("funny2","心中〜穏やかじゃあないですね〜");
		// 特定のユーザー番号の場合、エラー画面に飛ばす
		if (inputNo.equals("のび太君")) {
			redirectAttributes.addFlashAttribute("funny","君がのび太と名乗っても、ドラえもんはここにはいないよ。");
			return "redirect:error";
		} else if (inputNo.equals("しゃべる")) {
			redirectAttributes.addFlashAttribute("funny","へんじがない。ただのしかばねのようだ。");
			return "redirect:error";
		}
		 else if (inputNo.equals("寿限無")) {
				redirectAttributes.addFlashAttribute("funny","寿限無、寿限無\r\n五劫の擦り切れ\r\n海砂利水魚の\r\n水行末 雲来末 風来末\r\n食う寝る処に住む処\r\n藪ら柑子の藪柑子\r\nパイポパイポ パイポのシューリンガン\r\nシューリンガンのグーリンダイ\r\nグーリンダイのポンポコピーのポンポコナーの\r\n長久命の長助");
				return "redirect:error";
			}
		 else if (inputNo.equals("1/1")||inputNo.equals("伯林")) {
				redirectAttributes.addFlashAttribute("funny","年の始めの例とて　終なき世のめでたさを　松竹たてて門ごとに　祝う今日こそ楽しけれ　初日のひかりさしいでて\r\n"
						+ "四方に輝く　今朝のそら\r\n"
						+ "君がみかげに　比えつつ\r\n"
						+ "仰ぎ見るこそ　尊とけれ");
				redirectAttributes.addFlashAttribute("funny2","あけましておめでとうございます! だがエラーだ。");
				return "redirect:error";
			}
		 else if (inputNo.equals("code:")) {
				redirectAttributes.addFlashAttribute("funny","1986\n");
				return "redirect:error";
			} else if (inputNo.equals("brass")) {
				redirectAttributes.addFlashAttribute("funny"," ");
				redirectAttributes.addFlashAttribute("funny2","真鍮〜穏やかじゃあないですね〜");
				return "redirect:error";
			}
		
		// 入力データが取得できなかった場合、ページを初期化する
		if (inputNo.equals("") || inputPassword.equals("")) {
			model.addAttribute("nothing","ユーザー番号とパスワードを入力してください。");
			return "signin";
		}
		
		// リポジトリから、登録されているユーザーの情報を取得
		String resultMessage = repos.getAccountData(inquiryData, inputNo);
		// ユーザーが登録されていない場合、ページを初期化する
		if (!resultMessage.equals(WispagramRepository.successMessage)) {
			model.addAttribute("notFind", "ユーザー番号が登録されていません。");
			return "signin";
		}
		
		// 入力されたデータと登録されたデータを比較
		// 異なる場合、ページを初期化する
		if (!inputPassword.equals(inquiryData.getPassword())) {
			model.addAttribute("miss", "パスワードが間違っています。");
			return "signin";
		}
		
		// データ保持クラスから、loginメソッド呼び出し
		if (!repos.login(LoginUser.selfData, inputNo).equals(WispagramRepository.successMessage)) {
			return "signin";
		}
		LoginUser.isLogin = true;
		
		// ホーム画面遷移
		return "redirect:home";
	}
	
	private void RedirectAttribute(String string, String string2) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	/* サブログイン */
	
	// サブログイン、ページの初期化
	@GetMapping("/signin2")
	public String init2() {
		// ログイン状態の初期化
		LoginUser.isLogin=false;
        
		// signin2.htmlに画面遷移
		return "signin2";
	}
	
	// サブログイン画面
	@PostMapping("/signin2")
	public String subLogin(
			@RequestParam("ID")String inputNo,
			@RequestParam("subPasswordAnswer")String inputSubPasswordAnswer,
			Model model ) {
		
		// ユーザー番号が入力されていない場合
		// 処理を終了
		if (inputNo.equals("")) {
			model.addAttribute("nothing", "ユーザー番号を入力してください。");
			return "signin2";
		}
		
		// リポジトリから登録されているユーザーの情報を取得できなかった場合、処理を終了
		if(!repos.getAccountData(inquiryData, inputNo).equals(WispagramRepository.successMessage)) {
			model.addAttribute("notFind", "ユーザー番号が登録されていません。");
			return "signin2";
		}
		model.addAttribute("ID", inputNo); /* 質問文取得して渡す */
		model.addAttribute("subPasswordText", inquiryData.getSubPasswordText());
		
		// サブPWが入力されていない or アカウントデータと答えが異なる場合
		// ユーザー番号とサブPW本文を保持して、処理を終了
		if (!inputSubPasswordAnswer.equals(inquiryData.getSubPasswordAnswer())) {
			model.addAttribute("ID", inputNo);
			model.addAttribute("miss","質問の答えが間違っています。");
			return "signin2";
		}
		
		// ログイン
		//LoginUser.login(inputNo);
		if (!repos.login(LoginUser.selfData, inputNo).equals(WispagramRepository.successMessage)) {
			return "signin2";
		}
		LoginUser.isLogin = true;
		
		// ホーム画面遷移
		return "redirect:home";
	}
}
