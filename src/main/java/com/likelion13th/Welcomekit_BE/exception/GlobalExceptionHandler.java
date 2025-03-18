package com.likelion13th.Welcomekit_BE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.likelion13th.Welcomekit_BE.domain.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// ✅ Custom Exception 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
		ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode().name(), ex.getMessage());
		return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
	}

	// ✅ IllegalArgumentException 처리
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
		ErrorResponse errorResponse = new ErrorResponse("INVALID_ARGUMENT", ex.getMessage());
		return ResponseEntity.badRequest().body(errorResponse);
	}

	// ✅ NullPointerException 처리
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
		ErrorResponse errorResponse = new ErrorResponse("NULL_POINTER", "Unexpected null value encountered");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	// ✅ 기본적인 Exception 처리 (예상치 못한 오류)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
