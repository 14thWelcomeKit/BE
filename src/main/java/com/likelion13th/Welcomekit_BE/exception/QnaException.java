package com.likelion13th.Welcomekit_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class QnaException extends RuntimeException {

	private final HttpStatus httpStatus;
	private final String code;

	public QnaException(HttpStatus httpStatus, String code, String message) {
		super(message);
		this.httpStatus = httpStatus;
		this.code = code;
	}
}
