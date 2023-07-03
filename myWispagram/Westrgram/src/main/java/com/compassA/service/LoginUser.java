/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                           ログインユーザー                            */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/18            作成者:山谷開偉             */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
package com.compassA.service;

import org.springframework.stereotype.Service;

import com.compassA.model.UserData;

@Service

public class LoginUser {

	public static UserData	selfData = new UserData();
	public static boolean	isLogin = false;

}
