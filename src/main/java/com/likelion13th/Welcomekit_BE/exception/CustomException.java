package com.likelion13th.Welcomekit_BE.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getMessage()); // 부모 클래스(RuntimeException)에 메시지 전달
		this.errorCode = errorCode;
	}

	public HttpStatus getHttpStatus() {
		return errorCode.getHttpStatus();
	}
}
