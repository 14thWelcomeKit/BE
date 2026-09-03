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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/welcome")
@Tag(name = "환영 메시지", description = "환영 메시지 조회 및 읽음 처리 API.")
public class WelcomeMessageController {

	@Autowired
	private final WelcomeMessageManager welcomeMessageManager;

	@Operation(summary = "내 환영 메시지 조회", description = "로그인한 사용자에게 배정된 환영 메시지를 조회합니다.")
	@GetMapping("/message")
	public ResponseEntity<?> getMyWelcomeMessage(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(welcomeMessageManager.getMyWelcomeMessage(userDetails.getUsername()));
	}

	@Operation(summary = "환영 메시지 읽음 처리", description = "로그인한 사용자의 환영 메시지를 읽음 상태로 갱신합니다.")
	@PostMapping("/read")
	public ResponseEntity<?> updateWelcomeRead(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(welcomeMessageManager.updateHasReadWelcome(userDetails.getUsername()));
	}
}

