package com.likelion13th.Welcomekit_BE.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "로그인 요청 본문")
public class LoginRequest {

	@NotBlank
	@Email
	@Schema(description = "로그인 이메일(가입 시 등록한 이메일)", example = "hong@hufs.ac.kr", requiredMode = Schema.RequiredMode.REQUIRED)
	private String email;

	@NotBlank
	@Schema(description = "비밀번호", example = "myPassword123!", requiredMode = Schema.RequiredMode.REQUIRED)
	private String password;
}
