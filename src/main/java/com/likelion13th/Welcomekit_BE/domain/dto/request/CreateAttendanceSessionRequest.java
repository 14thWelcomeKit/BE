package com.likelion13th.Welcomekit_BE.domain.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 운영진이 특정 날짜의 출석 세션을 수동 생성할 때 사용.
 * sessionDate를 비우면 현재 시각으로 생성한다.
 */
@Getter
@Schema(description = "출석 세션(날짜) 수동 생성 요청 본문")
public class CreateAttendanceSessionRequest {

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	@Schema(description = "생성할 세션의 날짜/시각(ISO-8601). 비우면 현재 시각으로 생성됩니다.",
		example = "2026-03-02T19:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	private LocalDateTime sessionDate;
}
