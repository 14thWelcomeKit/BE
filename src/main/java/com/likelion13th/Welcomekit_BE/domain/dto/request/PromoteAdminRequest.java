package com.likelion13th.Welcomekit_BE.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PromoteAdminRequest {
	@NotNull
	private Long targetUserId;
}
