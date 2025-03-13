package com.likelion13th.Welcomekit_BE.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.service.AttendanceService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceManager {

	@Autowired
	private final AttendanceService attendanceService;

	public void generateQR(HttpServletResponse response) {
		attendanceService.generateQR(response);
	}
}
