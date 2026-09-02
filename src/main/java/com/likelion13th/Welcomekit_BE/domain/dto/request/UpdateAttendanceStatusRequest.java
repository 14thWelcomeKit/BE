package com.likelion13th.Welcomekit_BE.domain.dto.request;

import com.likelion13th.Welcomekit_BE.domain.enums.AttendanceStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 운영진이 특정 출석 레코드의 상태(출석/지각/결석)를 수정할 때 사용.
 */
@Getter
public class UpdateAttendanceStatusRequest {
	@NotNull
	private AttendanceStatus status;
}
