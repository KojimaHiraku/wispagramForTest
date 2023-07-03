/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           アカウント登録画面専用のデータ保持クラス                                */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/19            作成者:大濱由聖                      */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.model;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountRegistrationStorage {
	//長さ4固定
	@Length(min=4,max=4, message="ユーザー番号は4桁で入力してください。")
	private String signupUserNo;
	//Nullだとエラー
	@NotBlank(message="ユーザー名を入力してください。")
	@Length(min=0,max=30, message="ユーザ名は30文字以内で入力してください。")
	private String signupUserName;
	@NotBlank(message="パスワードを入力してください。")
	@Length(min=0,max=30, message="パスワードは30文字以内で入力してください。")
	private String signupPassword;
	@NotBlank(message="確認パスワードを入力してください。")
	@Length(min=0,max=30, message="確認パスワードは30文字以内で入力してください。")
	private String signupRePassword;
	@NotBlank(message="秘密の質問を入力してください。")
	@Length(min=0,max=30, message="秘密の質問は30文字以内で入力してください。")
	private String signupSubPasswordText;
	@NotBlank(message="質問の答えをを入力してください。")
	@Length(min=0,max=30, message="質問の答えは30文字以内で入力してください。")
	private String signupSubPasswordAnswer;
}
