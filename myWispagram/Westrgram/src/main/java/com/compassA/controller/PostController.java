/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                               投稿作成画面                            */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/20            作成者:草木                 */
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

import com.compassA.model.TweetData;
import com.compassA.repository.WispagramRepository;
import com.compassA.service.LoginUser;
@Controller
public class PostController {
	
	// 送信するツイート
	private TweetData tweetSource;
	
	// Autowire（クラス呼び出し簡略化）
	@Autowired
	private WispagramRepository repos;
	
	// ページの初期化
	@GetMapping("/posting")
	public String init(Model model) {
		// ユーザーがログインしていない状態であればログイン画面に遷移させる
        // ケース：URL直打ちによる直リンクなど
        if (!LoginUser.isLogin) {
            return "redirect:signin";
        }
		
        // posting.htmlに遷移
		model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
		return "posting";
	}
	
	// ツイートの送信
	@PostMapping("/posting")
	public String submitTweet(@RequestParam("text")String tweetText ,@RequestParam("img1") MultipartFile multipartFile, @RequestParam("img2") MultipartFile multipartFile2, Model model) throws IOException {
		// 文字を取得できなかった場合、処理を終了
		if (tweetText.isEmpty()) {
			model.addAttribute("NullTextPost","投稿する際はテキストを共に投稿してください");
			model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
			return "posting";
		}
		Date now = new Date();
		String NowString = now.toString();
		NowString =NowString.replace(":", "");
		///コロンが付くので、それを削除する
		//投稿時間を取得し、それを名前にくっ付ける。
		/*
		 * ローカルのimageフォルダにアップロードするためにパスを指定(ここは人によって変わる可能性あり)
		 * */
		Path dst;
		String imagesPath = "src\\main\\resources\\static\\images\\";
		String image1Path = "";
		String image2Path = "";
		if (!multipartFile.getOriginalFilename().isEmpty()) {
			// アップロード後の相対パス : プロジェクト → フォルダ
			dst = Path.of(imagesPath, NowString+multipartFile.getOriginalFilename());
			
			// ファイルをアップロードする処理
			Files.copy(multipartFile.getInputStream(), dst);
			
			// アップロード後の相対パス : HTML → フォルダ
			image1Path = "/images/" +NowString+ multipartFile.getOriginalFilename();
		}
		
		if (!multipartFile2.getOriginalFilename().isEmpty()) {
			// アップロード後の相対パス : プロジェクト → フォルダ
			dst = Path.of(imagesPath,NowString+ multipartFile2.getOriginalFilename());
			
			// ファイルをアップロードする処理
			Files.copy(multipartFile2.getInputStream(), dst);
			
			// アップロード後の相対パス : HTML → フォルダ
			image2Path = "/images/" + NowString+multipartFile2.getOriginalFilename();
		}
		
		// 送信するツイートに、ユーザー情報とツイート情報を追加
		tweetSource = new TweetData(LoginUser.selfData.getUserId(), tweetText, image1Path, image2Path);
		
		// 送信するツイートをDBに追加
		String resultMessage = repos.submitTweet(tweetSource);
		
		// 送信するツイートをDB送れなかった場合、文字を保持して処理を終了
		if (!resultMessage.equals(WispagramRepository.successMessage)) {
			model.addAttribute("text", tweetText);
			model.addAttribute("ImImg", LoginUser.selfData.getIconImage());
			return "posting";
		}
		
		// home.htmlに遷移
		return "redirect:home";
	}
	
}
