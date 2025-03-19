package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactController {
	@GetMapping({"/", "/{path:^(?!api$).*}"})
	public String forwardToReact() {
		return "forward:/index.html";
	}
}