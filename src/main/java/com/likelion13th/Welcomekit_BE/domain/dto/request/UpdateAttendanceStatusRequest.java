package com.likelion13th.Welcomekit_BE.domain.dto.request;

import com.likelion13th.Welcomekit_BE.domain.enums.AttendanceStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

/**
 * 운영진이 특정 출석 레코드의 상태(출석/지각/결석)를 수정할 때 사용.
 */
@Getter
@Schema(description = "출석 상태 수정 요청 본문")
public class UpdateAttendanceStatusRequest {

	@NotNull
	@Schema(description = "변경할 출석 상태 (PRESENT: 출석, LATE: 지각, ABSENT: 결석)",
		example = "PRESENT", requiredMode = Schema.RequiredMode.REQUIRED)
	private AttendanceStatus status;
}
