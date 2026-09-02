package com.likelion13th.Welcomekit_BE.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateAttendanceSessionRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.UpdateAttendanceStatusRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.MyAttendanceResponse;
import com.likelion13th.Welcomekit_BE.manager.AttendanceSessionManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
@Tag(name = "출석", description = "QR 기반 출석 체크와 개인 출석 조회, 운영진 전용 출석 세션·상태 관리 API.")
public class AttendanceSessionController {

	@Autowired
	private final AttendanceSessionManager attendanceSessionManager;

	@Operation(summary = "출석용 QR 이미지 생성", description = "오늘 세션의 토큰이 포함된 출석용 QR 이미지를 생성해 응답으로 내려줍니다.")
	@GetMapping("generate-qr")
	void generateQRCode(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
		attendanceSessionManager.generateQR(userDetails.getUsername(), response);
	}

	@Operation(summary = "출석 체크(QR 스캔)", description = "QR에 담긴 token으로 출석을 처리합니다. 그날 세션 토큰과 일치할 때만 인정되며, 불일치 시 최신 QR 재스캔이 필요합니다. 개인 출석 칸이 없으면 그 자리에서 생성합니다.")
	@PostMapping("/success")
	public ResponseEntity<String> qrSuccess(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam(value = "token", required = false) String token
	) {
		return ResponseEntity.ok(attendanceSessionManager.markAttendance(userDetails.getUsername(), token));
	}

	@Operation(summary = "내 출석 내역 조회", description = "로그인한 사용자의 전체 출석 세션별 출석 상태 목록을 조회합니다.")
	@GetMapping("/my-attendance")
	public ResponseEntity<?> getMyAttendance(
		@AuthenticationPrincipal UserDetails userDetails) {
		List<MyAttendanceResponse> myAttendance = attendanceSessionManager.getMyAttendance(userDetails.getUsername());
		return ResponseEntity.ok(myAttendance);
	}

	@Operation(summary = "오늘 내 출석 상태 조회", description = "로그인한 사용자의 오늘 세션 출석 상태를 조회합니다.")
	@GetMapping("/today/attendance")
	public ResponseEntity<?> getTodayAttendance(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(attendanceSessionManager.getTodayAttendance(userDetails.getUsername()));
	}

	// ══════════════════════════════════════════════════════════
	// 운영진(ADMIN) 전용 출석 관리 (C-1 + C-2)
	// ══════════════════════════════════════════════════════════

	@Operation(summary = "[운영진] 전체 출석 세션(날짜) 목록 조회")
	@GetMapping("/sessions")
	public ResponseEntity<?> getAllSessions(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(attendanceSessionManager.getAllSessions(userDetails.getUsername()));
	}

	@Operation(summary = "[운영진] 특정 세션의 출석 상세(사람별) 조회")
	@GetMapping("/sessions/{sessionId}")
	public ResponseEntity<?> getSessionAttendances(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long sessionId) {
		return ResponseEntity.ok(attendanceSessionManager.getSessionAttendances(userDetails.getUsername(), sessionId));
	}

	@Operation(summary = "[운영진] 특정 출석 레코드 상태 수정(출석/지각/결석)")
	@PatchMapping("/{attendanceId}/status")
	public ResponseEntity<?> updateAttendanceStatus(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long attendanceId,
		@Valid @RequestBody UpdateAttendanceStatusRequest request) {
		return ResponseEntity.ok(
			attendanceSessionManager.updateAttendanceStatus(userDetails.getUsername(), attendanceId, request.getStatus()));
	}

	@Operation(summary = "[운영진] 출석 세션(날짜) 수동 생성")
	@PostMapping("/sessions")
	public ResponseEntity<?> createSession(@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody CreateAttendanceSessionRequest request) {
		return ResponseEntity.ok(
			attendanceSessionManager.createSession(userDetails.getUsername(), request.getSessionDate()));
	}

	@Operation(summary = "[운영진] 출석 세션(날짜) 삭제")
	@DeleteMapping("/sessions/{sessionId}")
	public ResponseEntity<?> deleteSession(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long sessionId) {
		attendanceSessionManager.deleteSession(userDetails.getUsername(), sessionId);
		return ResponseEntity.ok("세션이 삭제되었습니다.");
	}
}
