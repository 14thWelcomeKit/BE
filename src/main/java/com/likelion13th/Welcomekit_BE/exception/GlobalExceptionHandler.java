package com.likelion13th.Welcomekit_BE.exception;

import org.springframework.dao.DataIntegrityViolationException;
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

	// // ✅ IllegalArgumentException 처리
	// @ExceptionHandler(RuntimeException.class)
	// public ResponseEntity<ErrorResponse> handleRunException(RuntimeException ex) {
	// 	ErrorResponse errorResponse = new ErrorResponse("RUNTIME", ex.getMessage());
	// 	return ResponseEntity.badRequest().body(errorResponse);
	// }

	// ✅ DataIntegrityViolationException 처리 (DB 무결성 제약 위반)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
		ErrorResponse errorResponse = new ErrorResponse("DUPLICATE_ENTRY", ex.getMessage());
		return ResponseEntity.badRequest().body(errorResponse);
	}

	// ✅ NullPointerException 처리
	// @ExceptionHandler(NullPointerException.class)
	// public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
	// 	ErrorResponse errorResponse = new ErrorResponse("UNAUTHORIZED_USER", "User authentication is required.");
	// 	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	// }

	// ✅ 기본적인 Exception 처리 (예상치 못한 오류)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		ErrorResponse errorResponse = new ErrorResponse("INTERNAL_SERVER_ERROR", ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}
