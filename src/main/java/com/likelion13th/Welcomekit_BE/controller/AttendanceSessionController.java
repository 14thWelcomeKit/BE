package com.likelion13th.Welcomekit_BE.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.response.MyAttendanceResponse;
import com.likelion13th.Welcomekit_BE.manager.AttendanceSessionManager;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceSessionController {

	@Autowired
	private final AttendanceSessionManager attendanceSessionManager;

	@GetMapping("generate-qr")
	void generateQRCode(HttpServletResponse response) {
		attendanceSessionManager.generateQR(response);
	}

	@GetMapping("/success")
	public ResponseEntity<String> qrSuccess(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam Long sessionId
	) {
		return ResponseEntity.ok(attendanceSessionManager.markAttendance(userDetails.getUsername(), sessionId));
	}

	@GetMapping("/my-attendance")
	public ResponseEntity<?> getMyAttendance(
		@AuthenticationPrincipal UserDetails userDetails) {
		List<MyAttendanceResponse> myAttendance = attendanceSessionManager.getMyAttendance(userDetails.getUsername());
		return ResponseEntity.ok(myAttendance);
	}
}
