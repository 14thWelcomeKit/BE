package com.likelion13th.Welcomekit_BE.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "이메일 인증코드 확인 요청 본문")
public class VerifyEmailCodeRequest {

	@NotBlank
	@Email
	@Schema(description = "인증코드를 발송받은 이메일", example = "hong@hufs.ac.kr", requiredMode = Schema.RequiredMode.REQUIRED)
	private String email;

	@NotBlank
	@Schema(description = "메일로 받은 6자리 인증코드", example = "482913", requiredMode = Schema.RequiredMode.REQUIRED)
	private String code;
}
