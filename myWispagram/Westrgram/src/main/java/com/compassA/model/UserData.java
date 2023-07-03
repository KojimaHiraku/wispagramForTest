/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                             ユーザーのデータ                              */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/17            作成者:大濱由聖                      */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.model;

import lombok.AllArgsConstructor;
import lombok.Data;

//DataはLombokのアノテーション。getterやsetterを自動生成する。
/*AllArgsConstructorはLombokアノテーション。
 * 全ての変数へ値をセットするための引数付きコンストラクタを自動生成する
 */
@Data
@AllArgsConstructor
public class UserData {
	public UserData() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	private int userId;
	private String userNo;
	private String userName;
	private String profileText;
	private String iconImage;
	private boolean adminAuthority;
	private boolean deletedFlg;
}
