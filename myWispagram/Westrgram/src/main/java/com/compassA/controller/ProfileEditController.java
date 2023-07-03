/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           プロフィール編集画面コントローラー                                */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/20            作成者:大濱由聖                      */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.compassA.model.EditableProfileData;
import com.compassA.repository.WispagramRepository;
import com.compassA.service.LoginUser;

@Controller
public class ProfileEditController {
	
	//編集可能なプロフィール情報クラス
	EditableProfileData userData = new  EditableProfileData();
	
	//プロフィール画面からuserIdを受け取る用の変数
	private int userID;
	
	//クラスの呼び出しを簡略化するアノテーション
	@Autowired
	public WispagramRepository repository;
	@Autowired
	public ProfileController profile;
	
		
	@GetMapping("/profile_edit")
	public String init(Model model) {
		//ログインチェック
		if (!LoginUser.isLogin) {
			return "redirect:signin";
		}
		
		//userIDをProfileControllerからゲッターで取得
		userID = profile.getUserId();
		//IDが初期値の場合ログインユーザーのデータを取得
		if(userID==0) userID=LoginUser.selfData.getUserId();
		
		//IDを指定して編集可能なプロフィール情報を格納
		repository.getEditableUserProfile(userData, userID);
		
		/*プロフィール画面のユーザーIDのアカウント情報をDBから持ってきて入力欄に入力する*/
		/*入力する内容は、ユーザー画像、ユーザー名、自己紹介、秘密の質問*/
		
		model.addAttribute("image" ,userData.getIconImage());
		model.addAttribute("name", userData.getUserName());
		model.addAttribute("text", userData.getProfileText());
		model.addAttribute("subPassText",userData.getSubPasswordText());
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
				
		return "profile_edit";
	}
	
	/*ホームに遷移するボタン、ブックマーク一覧に遷移するボタン、検索窓用の機能はTweetServiceで実装？*/
	
	@PostMapping("/profile_edit")
	public String updateProfile(@RequestParam("Usericondata") MultipartFile UsericondataFile,
											 @RequestParam("name") String userName,
											 @RequestParam("text") String profileText,
											 @RequestParam("check") String checkPassword,
											 @RequestParam("pass") String password,
											 @RequestParam("rePass") String rePassword,
											 @RequestParam("subPassText") String subPasswordText,
											 @RequestParam("subPassAns") String subPasswordAnswer,
											 Model model) throws IOException {
		
		//ユーザー画像、ユーザー名、自己紹介文をset
		//userData.setIconImage(iconImage);
		userData.setUserName(userName);
		userData.setProfileText(profileText);
		
		Path dst;
		String imagesPath = "src\\main\\resources\\static\\images\\";
		String image1Path = userData.getIconImage();
		if (!UsericondataFile.getOriginalFilename().isEmpty()) {
			String timeString = new Date().toString().replace(":","");
			
			// アップロード後の相対パス : プロジェクト → フォルダ
			dst = Path.of(imagesPath,timeString+UsericondataFile.getOriginalFilename());
			
			// ファイルをアップロードする処理
			Files.copy(UsericondataFile.getInputStream(), dst);
			
			// アップロード後の相対パス : UsericondataHTML → フォルダ
			image1Path = "/images/" +timeString+UsericondataFile.getOriginalFilename();
		}
		userData.setIconImage(image1Path);
		/*++++++++++++++++++++++++++++
		 * postリクエストをした際に入力内容が
		 * 削除されないようにmodelで入力し直す
		 * ++++++++++++++++++++++++++++*/
		
		model.addAttribute("image", image1Path);
		model.addAttribute("name", userName);
		model.addAttribute("text", profileText);
		model.addAttribute("subPassText", subPasswordText);
		
		/*++++++++++++++++++++*/
		//パスワードの変更がある場合
		/*++++++++++++++++++++*/
		
		//なぜかパスワード関連の変更がない
		if(password.isEmpty() && 
			subPasswordAnswer.isEmpty() && 
			userData.getSubPasswordText().equals(subPasswordText)) {
				repository.confirmEdit(userData);
				repository.login(LoginUser.selfData, LoginUser.selfData.getUserNo());
				return "redirect:profile";
		}
		
		//変更があったが、確認用パスワードが未入力だった場合
		if(checkPassword.isEmpty()) {
			model.addAttribute("empty", "現在のパスワードまたは秘密の答えを入力してください。");
			return "profile_edit";
		}
		
		//確認用パスワードに入力したパスワードが一致しない場合。
		if(!(userData.getPassword().equals(checkPassword) ||
		   userData.getSubPasswordAnswer().equals(checkPassword))) {
			 model.addAttribute("miss", "登録したパスワードまたは秘密の答えと一致しません。");
			 return "profile_edit";
		}
		
		/*++++++++++++++++++++*/
		//↓↓パスワードを変更する↓↓
		/*++++++++++++++++++++*/
		
		//パスワードの変更がある場合
		if(!password.isEmpty()) {
			if(password.equals(rePassword)) {
				userData.setPassword(password);
			} else {
				model.addAttribute("notEquals","パスワードの再入力が一致していません。");
				return "profile_edit";
			}
		}
		
		//秘密の質問空欄でなかったら場合
		if(!subPasswordText.isEmpty()) {
			userData.setSubPasswordText(subPasswordText);
		}
		
		//秘密の答えの変更がある場合
		if(!subPasswordAnswer.isEmpty()) {
			userData.setSubPasswordAnswer(subPasswordAnswer);
		}
		
		//EditableProfileDataの情報をリポジトリに渡す。
		repository.confirmEdit(userData);
		repository.login(LoginUser.selfData, LoginUser.selfData.getUserNo());
		
		return "redirect:profile";
	}
	
	/* +++++++++++
	 * アカウント削除
	 * +++++++++++*/
	
	@PostMapping("/deleteAccount")
	public String deleteUser() {
		repository.deleteAccount(userID);
		return "redirect:signin";
	}
}
