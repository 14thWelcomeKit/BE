package com.likelion13th.Welcomekit_BE.exception;

import lombok.Getter;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User does not exist"),
	INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "Invalid argument provided"),
	NULL_POINTER(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected null value encountered"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"),
	CUSTOM_ERROR(HttpStatus.BAD_REQUEST, "This is a custom error"),
	PERMISSION_ERROR(HttpStatus.BAD_REQUEST, "Permission error"),
	SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "Session does not exist"),
	TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "Team does not exist"),
	CELL_NOT_FOUND(HttpStatus.NOT_FOUND, "Cell does not exist")
	;


	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}