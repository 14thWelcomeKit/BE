package com.likelion13th.Welcomekit_BE.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * 비밀번호 재설정(찾기) 요청.
 * 이메일 인증코드 검증(POST /api/auth/email/verify-code)이 완료된 상태에서 호출되며,
 * 현재 비밀번호 없이 새 비밀번호로 재설정한다.
 */
@Getter
public class ResetPasswordRequest {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String newPassword;
}
