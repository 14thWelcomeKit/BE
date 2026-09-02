package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * 운영진용 출석 세션(날짜) 요약. 세션 목록 조회에 사용.
 */
@Getter
@Builder
public class AttendanceSessionSummaryResponse {
	private Long sessionId;
	private LocalDateTime sessionDate;
	private long totalCount;
	private long presentCount;
	private long lateCount;
	private long absentCount;
}
