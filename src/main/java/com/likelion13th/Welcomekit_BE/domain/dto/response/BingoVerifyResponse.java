package com.likelion13th.Welcomekit_BE.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BingoVerifyResponse {
	private Integer cellId;
	private String status;
	private String matchedWithName;
	private String expiresAt;
}
