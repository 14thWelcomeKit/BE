package com.likelion13th.Welcomekit_BE.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
	private String errorCode;
	private String message;
}
