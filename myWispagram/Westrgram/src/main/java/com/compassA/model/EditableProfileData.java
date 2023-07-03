/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                      編集可能なプロフィールデータ                     */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/17            作成者:草木                 */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditableProfileData {
	public EditableProfileData() {
		
	}
	private int userId;
	private String userName;
	private String profileText;
	private String iconImage;
	private String password;
	private String subPasswordText;
	private String subPasswordAnswer;
}
