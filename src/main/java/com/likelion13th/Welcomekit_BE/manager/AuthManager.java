package com.likelion13th.Welcomekit_BE.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.LoginRequest;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.jwt.JwtAuthenticationResponse;
import com.likelion13th.Welcomekit_BE.jwt.JwtTokenProvider;
import com.likelion13th.Welcomekit_BE.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthManager {

	@Autowired
	private final AuthService authService;
	@Autowired
	private final JwtTokenProvider tokenProvider;
	@Autowired
	private final PasswordEncoder passwordEncoder;

	public JwtAuthenticationResponse authenticateUser(@Valid LoginRequest loginRequest) {
		// 1) 이메일로 사용자 조회 (없으면 로그인 실패)
		//    존재 여부 노출을 피하기 위해 "사용자 없음"과 "비밀번호 불일치"를 동일한 LOGIN_FAILED(401)로 응답한다.
		User user;
		try {
			user = authService.findByEmail(loginRequest.getEmail());
		} catch (UsernameNotFoundException e) {
			throw new CustomException(ErrorCode.LOGIN_FAILED);
		}

		// 2) 비밀번호 검증 (불일치 시 로그인 실패)
		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new CustomException(ErrorCode.LOGIN_FAILED);
		}

		// 3) JWT 발급
		try {
			String jwt = tokenProvider.generateToken(user);
			return new JwtAuthenticationResponse(jwt);
		} catch (Exception e) {
			// 토큰 생성 등 예상치 못한 서버 오류만 500으로 처리
			log.error("로그인 처리 중 오류 발생: email={}", loginRequest.getEmail(), e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
