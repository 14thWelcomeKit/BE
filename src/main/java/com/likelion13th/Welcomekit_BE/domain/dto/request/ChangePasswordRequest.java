package com.likelion13th.Welcomekit_BE.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
	private String newPassword;
	private String currentPassword;
}
