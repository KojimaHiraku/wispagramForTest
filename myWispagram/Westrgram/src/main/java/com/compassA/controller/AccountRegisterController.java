/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           アカウント登録画面                                */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/19            作成者: 大濱由聖                     */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.compassA.model.AccountRegistration;
import com.compassA.model.AccountRegistrationStorage;
import com.compassA.repository.WispagramRepository;

@Controller
public class AccountRegisterController {
	
	//Autowiredはクラスの呼び出しを簡略化するアノテーション
	@Autowired
	public WispagramRepository repository;
	
	//クライアントからURLを受け取る
	@GetMapping("/signup")
	public String init(@ModelAttribute("storage") AccountRegistrationStorage storage) {
		//アカウント登録画面に遷移
		return "signup";
	}
	
	//htmlから送られた情報を受け取る
	@PostMapping("/signup")
	public String signUp(@ModelAttribute("storage") @Validated AccountRegistrationStorage storage, 
									BindingResult result,
									Model model,
									RedirectAttributes redirectAttribute) {
		
		//入力ミス判定用フラグ
		boolean errorflag = false;
		//バリデーションエラー処理
		if(result.hasErrors()) {
			List<String> errorList = new ArrayList<>();
			 for (ObjectError error : result.getAllErrors()) {
	                errorList.add(error.getDefaultMessage());
	            }
			model.addAttribute("validationError",errorList);
			errorflag = true;
		}
		if(storage.getSignupPassword().equals(storage.getSignupRePassword()) == false) {
			model.addAttribute("missMatch","パスワードの再入力が一致しません。");
			errorflag = true;
		}
		
		//リポジトリに渡す用のオブジェクト(DB登録用)
		AccountRegistration data = new AccountRegistration();
		data.setUserNo(storage.getSignupUserNo());
		data.setUserName(storage.getSignupUserName());
		data.setPassword(storage.getSignupPassword());
		data.setSubPasswordText(storage.getSignupSubPasswordText());
		data.setSubPasswordAnswer(storage.getSignupSubPasswordAnswer());
		
		//データがすでに被っていないかのチェックするためのメソッド
		if(!repository.checkDataIntegrity(data).equals(WispagramRepository.successMessage)) {
			model.addAttribute("duplication","そのユーザー番号はすでに使われています。");
			errorflag=true;
		}
		if(errorflag) return "signup";
		//Repositoryに値を渡す
		repository.signUp(data);
		redirectAttribute.addFlashAttribute("success","アカウントの作成に成功しました！");
		return "redirect:signin";
	}
}
