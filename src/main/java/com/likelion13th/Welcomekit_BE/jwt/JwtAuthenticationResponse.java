package com.likelion13th.Welcomekit_BE.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthenticationResponse {
	private String accessToken;
	private String tokenType = "Bearer";
	private String userId;

	public JwtAuthenticationResponse(String accessToken, String userId) {
		this.accessToken = accessToken;
		this.userId = userId;
	}
}
