/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                                                                       */
/*                                                                       */
/*                              エラー画面                               */
/*                                                                       */
/*                                                                       */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
/*                     2023/04/21            作成者:草木                 */
/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

package com.compassA.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class Nobita {
	// ページの初期化
	@GetMapping("/error")
	public String init(@ModelAttribute("funny")String funny, Model model) {
		model.addAttribute("funny", funny);
		
		return "error";
	}
	
	@GetMapping("/goSignin")
	public String goSignin() {
		return "redirect:signin";
	}

}
