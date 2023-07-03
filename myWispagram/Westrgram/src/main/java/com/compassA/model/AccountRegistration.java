/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           アカウント登録データ	                     */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/17            作成者: 山谷 開偉           */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.model;

import lombok.Data;

@Data
public class AccountRegistration{
private String userNo;
private String userName;
private String password;
private String subPasswordText;
private String subPasswordAnswer;
}
