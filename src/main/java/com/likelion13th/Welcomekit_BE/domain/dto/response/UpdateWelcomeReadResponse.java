package com.likelion13th.Welcomekit_BE.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateWelcomeReadResponse {

	private String message;
	private Boolean hasReadWelcome;
}

