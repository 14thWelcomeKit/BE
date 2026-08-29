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
	INVALID_QR_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 QR입니다. 최신 QR을 다시 스캔해주세요."),
	TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "Team does not exist"),
	CELL_NOT_FOUND(HttpStatus.NOT_FOUND, "Cell does not exist"),
	PASSWORD_NOT_MATCHES(HttpStatus.BAD_REQUEST, "Password is not correct"),
	PROFILE_UPDATE_ERROR(HttpStatus.BAD_REQUEST, "Profile update Error"),
	INVALID_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "INVALID IMAGE FORMAT"),
	IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "IMAGE NOT FOUND"),
	UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "User authentication is required."),
	WELCOME_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Welcome message does not exist"),

	// ── 회원가입 / 이메일 인증 ──
	EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
	STUDENT_NUM_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 학번입니다."),
	INVALID_EMAIL_DOMAIN(HttpStatus.BAD_REQUEST, "허용되지 않은 이메일 도메인입니다."),
	EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "이메일 인증이 완료되지 않았습니다."),
	VERIFICATION_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "인증 요청 내역이 없습니다. 인증코드를 다시 요청해주세요."),
	VERIFICATION_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "인증코드가 만료되었습니다. 다시 요청해주세요."),
	VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "인증코드가 일치하지 않습니다."),
	EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "인증 메일 발송에 실패했습니다."),
	INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, "운영진 초대코드가 올바르지 않습니다."),

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