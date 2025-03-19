package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class ReactController {
	// @GetMapping({"/", "/{path:^(?!api$).*}"})
	// public String forwardToReact() {
	// 	return "forward:/index.html";
	// }
}