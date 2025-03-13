package com.likelion13th.Welcomekit_BE.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;
import com.likelion13th.Welcomekit_BE.service.AttendanceService;
import com.likelion13th.Welcomekit_BE.service.AttendanceSessionService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceManager {

	@Autowired
	private final AttendanceService attendanceService;
	@Autowired
	private final AttendanceSessionService attendanceSessionService;

	public void generateQR(HttpServletResponse response) {
		AttendanceSession session = attendanceSessionService.getTodaySession();
		attendanceService.generateQR(response, session.getId());
	}
}
