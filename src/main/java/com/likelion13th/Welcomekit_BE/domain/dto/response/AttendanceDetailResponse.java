package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.time.LocalDateTime;

import com.likelion13th.Welcomekit_BE.domain.enums.AttendanceStatus;

import lombok.Builder;
import lombok.Getter;

/**
 * 운영진용 개별 출석 레코드 상세. 세션 상세 조회 및 상태 수정 응답에 사용.
 */
@Getter
@Builder
public class AttendanceDetailResponse {
	private Long attendanceId;
	private Long userId;
	private String name;
	private String studentNum;
	private String teamName;
	private AttendanceStatus status;
	private LocalDateTime attendanceTime;
}
