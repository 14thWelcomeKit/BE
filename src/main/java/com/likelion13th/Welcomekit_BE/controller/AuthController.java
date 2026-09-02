package com.likelion13th.Welcomekit_BE.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.request.LoginRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.SendVerificationCodeRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.VerifyEmailCodeRequest;
import com.likelion13th.Welcomekit_BE.jwt.JwtAuthenticationResponse;
import com.likelion13th.Welcomekit_BE.manager.AuthManager;
import com.likelion13th.Welcomekit_BE.service.EmailVerificationService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	@Autowired
	private final AuthManager authManager;

	@Autowired
	private final EmailVerificationService emailVerificationService;

	@PostMapping("/sign-in")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		JwtAuthenticationResponse jwtAuthenticationResponse = authManager.authenticateUser(loginRequest);
		return ResponseEntity.ok(jwtAuthenticationResponse);
	}

	@Operation(summary = "이메일 인증코드 발송", description = "회원가입 전 학교 이메일로 인증코드를 발송합니다.")
	@PostMapping("/email/send-code")
	public ResponseEntity<?> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
		emailVerificationService.sendCode(request.getEmail());
		return ResponseEntity.ok("인증코드가 발송되었습니다.");
	}

	@Operation(summary = "이메일 인증코드 확인", description = "발송된 인증코드를 검증합니다.")
	@PostMapping("/email/verify-code")
	public ResponseEntity<?> verifyEmailCode(@Valid @RequestBody VerifyEmailCodeRequest request) {
		emailVerificationService.verifyCode(request.getEmail(), request.getCode());
		return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
	}

}
