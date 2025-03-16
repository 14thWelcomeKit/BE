package com.likelion13th.Welcomekit_BE.domain.dto.request;

import com.likelion13th.Welcomekit_BE.domain.enums.DevPart;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;

import lombok.Getter;

@Getter
public class CreateUserRequest {
	private String name;
	private String studentNum;
	private String password;
	private UserType userType;
	private DevPart devPart;
}
