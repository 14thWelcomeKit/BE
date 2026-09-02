package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.manager.WelcomeMessageManager;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/welcome")
public class WelcomeMessageController {

	@Autowired
	private final WelcomeMessageManager welcomeMessageManager;

	@GetMapping("/message")
	public ResponseEntity<?> getMyWelcomeMessage(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(welcomeMessageManager.getMyWelcomeMessage(userDetails.getUsername()));
	}

	@PostMapping("/read")
	public ResponseEntity<?> updateWelcomeRead(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(welcomeMessageManager.updateHasReadWelcome(userDetails.getUsername()));
	}
}

