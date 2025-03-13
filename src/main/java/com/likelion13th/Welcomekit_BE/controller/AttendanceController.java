package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.manager.AttendanceManager;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

	@Autowired
	private final AttendanceManager attendanceManager;

	@GetMapping("generate-qr")
	void generateQRCode(HttpServletResponse response) {
		attendanceManager.generateQR(response);
	}

	@GetMapping("/success")
	public ResponseEntity<String> qrSuccess(@AuthenticationPrincipal UserDetails userDetails) {
		String username = userDetails.getUsername();  // 세션에서 자동으로 유저 이름 가져옴

		// 여기서 username을 이용한 출석체크 로직 작성
		// attendanceService.markAttendance(username);

		return ResponseEntity.ok(username + "님, 출석이 완료되었습니다!");
	}
}
