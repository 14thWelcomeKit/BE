package com.likelion13th.Welcomekit_BE.domain.dto;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import lombok.Getter;

/**
 * /api/v3/welcome-kit 이하 신규 API 의 공통 응답 래퍼.
 *
 * <pre>
 * {
 *   "code": "S200",
 *   "message": "...",
 *   "data": { ... } | null,
 *   "errors": null,
 *   "timestamp": "2026-09-02T16:15:00+09:00"
 * }
 * </pre>
 *
 * 성공 시 {@code data} 를 채우고, 실패 시 {@code data} 는 {@code null} 로 둔다.
 */
@Getter
public class ApiResponse<T> {

	private static final ZoneId KST = ZoneId.of("Asia/Seoul");

	private final String code;
	private final String message;
	private final T data;
	private final Object errors;
	private final String timestamp;

	private ApiResponse(String code, String message, T data, Object errors) {
		this.code = code;
		this.message = message;
		this.data = data;
		this.errors = errors;
		this.timestamp = OffsetDateTime.now(KST)
			.truncatedTo(ChronoUnit.SECONDS)
			.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

	public static <T> ApiResponse<T> success(String code, String message, T data) {
		return new ApiResponse<>(code, message, data, null);
	}

	public static ApiResponse<Object> error(String code, String message) {
		return new ApiResponse<>(code, message, null, null);
	}
}
