package com.likelion13th.Welcomekit_BE.domain.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

/**
 * 운영진이 특정 날짜의 출석 세션을 수동 생성할 때 사용.
 * sessionDate를 비우면 현재 시각으로 생성한다.
 */
@Getter
public class CreateAttendanceSessionRequest {
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime sessionDate;
}
