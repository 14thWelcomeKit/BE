package com.likelion13th.Welcomekit_BE.domain.dto.response;

import com.likelion13th.Welcomekit_BE.domain.enums.AttendanceStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetTodayAttendanceResponse {
	private String teamName;
	private String name;
	private AttendanceStatus attendanceStatus;
}
