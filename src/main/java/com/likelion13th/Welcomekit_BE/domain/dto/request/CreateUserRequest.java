package com.likelion13th.Welcomekit_BE.domain.dto.request;

import com.likelion13th.Welcomekit_BE.domain.enums.DevPart;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateUserRequest {
	@NotBlank
	private String name;

	@NotBlank
	private String studentNum;

	@NotBlank
	private String password;

	@NotBlank
	@Email
	private String email;

	// 운영진 가입용 초대코드(선택). 없으면 무조건 BABY_LION으로 가입된다.
	private String inviteCode;

	private DevPart devPart;

	// userType은 더 이상 클라이언트가 지정하지 않는다.
	// 초대코드 유무에 따라 서버가 BABY_LION / ADMIN 을 결정한다.
}
