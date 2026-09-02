package com.likelion13th.Welcomekit_BE.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "이메일 인증코드 발송 요청 본문")
public class SendVerificationCodeRequest {

	@NotBlank
	@Email
	@Schema(description = "인증코드를 받을 이메일. 허용 도메인이 설정된 경우 해당 도메인만 가능", example = "hong@hufs.ac.kr", requiredMode = Schema.RequiredMode.REQUIRED)
	private String email;
}
