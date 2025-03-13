package com.likelion13th.Welcomekit_BE.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LoginRequest {
	private String studentNum;
	private String password;
}