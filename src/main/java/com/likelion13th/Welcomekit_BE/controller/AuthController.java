package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.request.LoginRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.ResetPasswordRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.SendVerificationCodeRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.VerifyEmailCodeRequest;
import com.likelion13th.Welcomekit_BE.jwt.JwtAuthenticationResponse;
import com.likelion13th.Welcomekit_BE.manager.AuthManager;
import com.likelion13th.Welcomekit_BE.service.EmailVerificationService;
import com.likelion13th.Welcomekit_BE.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "인증/이메일", description = "로그인(JWT 발급)과 회원가입용 이메일 인증코드 발송·확인 API. 로그인 식별자는 이메일 기준입니다.")
public class AuthController {

	@Autowired
	private final AuthManager authManager;

	@Autowired
	private final EmailVerificationService emailVerificationService;

	@Autowired
	private final UserService userService;

	@Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다. 성공 시 JWT(Access Token)를 발급하며, 토큰의 주체(principal)는 이메일 기준입니다.")
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

	@Operation(summary = "비밀번호 재설정용 인증코드 발송", description = "가입된 이메일로 비밀번호 재설정 인증코드를 발송합니다. (가입되지 않은 이메일이면 에러)")
	@PostMapping("/reset-password/send-code")
	public ResponseEntity<?> sendResetCode(@Valid @RequestBody SendVerificationCodeRequest request) {
		emailVerificationService.sendCodeForReset(request.getEmail());
		return ResponseEntity.ok("인증코드가 발송되었습니다.");
	}

	@Operation(summary = "비밀번호 재설정", description = "이메일 인증코드 검증 완료 후, 현재 비밀번호 없이 새 비밀번호로 재설정합니다.")
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
		userService.resetPassword(request.getEmail(), request.getNewPassword());
		return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
	}

}
