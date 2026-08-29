package com.likelion13th.Welcomekit_BE.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SendVerificationCodeRequest {
	@NotBlank
	@Email
	private String email;
}
