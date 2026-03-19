package com.likelion13th.Welcomekit_BE.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

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
	CELL_NOT_FOUND(HttpStatus.NOT_FOUND, "Cell does not exist"),
	PASSWORD_NOT_MATCHES(HttpStatus.BAD_REQUEST, "Password is not correct"),
	PROFILE_UPDATE_ERROR(HttpStatus.BAD_REQUEST, "Profile update Error"),
	INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "INVALID IMAGE FORMAT"),
	IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "IMAGE NOT FOUND"),
	UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "User authentication is required."),
	WELCOME_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Welcome message does not exist"),

	// ── 14기 빙고 V1 전용 에러코드 ──
	CELL_ALREADY_OCCUPIED(HttpStatus.CONFLICT, "해당 칸은 이미 점유 완료되었습니다."),
	NOT_CELL_OWNER(HttpStatus.FORBIDDEN, "해당 칸의 소유 팀이 아니거나 진행 중인 상태가 아닙니다."),
	USE_PATCH_FOR_UPDATE(HttpStatus.BAD_REQUEST, "본인 팀의 진행 중인 사진은 PATCH 엔드포인트로 수정해주세요."),
	UPDATE_WINDOW_EXPIRED(HttpStatus.BAD_REQUEST, "12시간 독점 기간이 이미 만료되었습니다."),
	TEAM_NOT_ASSIGNED(HttpStatus.BAD_REQUEST, "팀에 배정되지 않은 사용자입니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}