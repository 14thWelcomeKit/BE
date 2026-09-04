package com.likelion13th.Welcomekit_BE.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

	@Operation(
		summary = "출석용 QR 이미지 생성",
		description = """
			오늘 세션의 고유 토큰이 포함된 출석용 QR 이미지를 생성해 이미지(byte) 로 응답합니다. **로그인 필요.**

			- QR에는 `qr-base-url + "?token={오늘 세션 토큰}"` 형태의 URL이 인코딩됩니다.
			- 토큰은 세션(날짜)마다 다르므로, 과거에 캡처한 QR은 사용할 수 없습니다.
			- 응답은 JSON이 아닌 **이미지 바이너리**입니다.
			""")
	@GetMapping("generate-qr")
	void generateQRCode(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) {
		attendanceSessionManager.generateQR(userDetails.getUsername(), response);
	}

	@Operation(
		summary = "출석 체크 (QR 스캔 처리)",
		description = """
			QR에 담긴 `token` 으로 로그인 사용자의 출석을 처리합니다. **로그인 필요.**

			- 전달된 `token` 이 오늘 세션의 토큰과 일치할 때만 출석으로 인정됩니다.
			- 토큰 불일치 시 `INVALID_QR_TOKEN` 을 반환하며, 최신 QR을 다시 스캔해야 합니다. (과거/타 세션 QR 재사용 차단)
			- `token` 이 없으면(구버전 QR 호환) 토큰 검증을 건너뜁니다.
			- 개인 출석 칸이 없으면 그 자리에서 생성한 뒤 출석 처리합니다.

			**쿼리 파라미터**: `token` (선택) — QR에 포함된 세션 토큰

			**주요 에러**
			- `INVALID_QR_TOKEN` : 유효하지 않은 QR(최신 QR 재스캔 필요)
			- `SESSION_NOT_FOUND` : 오늘 세션이 없음
			""")
	@PostMapping("/success")
	public ResponseEntity<String> qrSuccess(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestParam(value = "token", required = false) String token
	) {
		return ResponseEntity.ok(attendanceSessionManager.markAttendance(userDetails.getUsername(), token));
	}

	@Operation(
		summary = "내 출석 내역 조회 (세션 날짜 최신순, 페이지네이션)",
		description = """
			로그인한 사용자의 출석 내역을 세션 날짜 **최신순**으로 반환합니다. **로그인 필요.**

			- 페이지네이션 지원: `page`(0부터), `size`(기본 20)
			- 응답은 Spring `Page` 형식(content, totalElements, totalPages, number, size 등)
			""")
	@GetMapping("/my-attendance")
	public ResponseEntity<?> getMyAttendance(
		@AuthenticationPrincipal UserDetails userDetails,
		@PageableDefault(size = 20) Pageable pageable) {
		Page<MyAttendanceResponse> myAttendance =
			attendanceSessionManager.getMyAttendance(userDetails.getUsername(), pageable);
		return ResponseEntity.ok(myAttendance);
	}

	@Operation(
		summary = "오늘 출석 현황 조회 (전체 명단)",
		description = """
			오늘의 가장 최근 출석 세션 1개에 대한 전체 출석 현황(사람별)을 반환합니다. **로그인 필요.**

			- 오늘 세션이 여러 개여도 최신 세션 1개의 명단만 반환합니다.
			- 팀 미배정자도 포함되며 `teamName`은 null로 내려갑니다.
			- 오늘 세션이 없으면 `SESSION_NOT_FOUND`(404).
			""")
	@GetMapping("/today/attendance")
	public ResponseEntity<?> getTodayAttendance(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(attendanceSessionManager.getTodayAttendance(userDetails.getUsername()));
	}

	// ══════════════════════════════════════════════════════════
	// 운영진(ADMIN) 전용 출석 관리
	// ══════════════════════════════════════════════════════════

	@Operation(
		summary = "[운영진] 전체 출석 세션(날짜) 목록 조회",
		description = """
			전체 출석 세션(날짜) 목록을 최신순으로 조회합니다. **운영진(ADMIN) 권한 필요.**

			- 각 세션별 상태 집계(출석/지각/결석 인원수)를 함께 반환합니다.

			**주요 에러**: `PERMISSION_ERROR` (운영진 아님)
			""")
	@GetMapping("/sessions")
	public ResponseEntity<?> getAllSessions(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(attendanceSessionManager.getAllSessions(userDetails.getUsername()));
	}

	@Operation(
		summary = "[운영진] 특정 세션의 출석 상세(사람별) 조회",
		description = """
			특정 세션(날짜)의 사람별 출석 상세 목록을 조회합니다. **운영진(ADMIN) 권한 필요.**

			**경로 변수**: `sessionId` — 조회할 세션 ID

			**주요 에러**: `SESSION_NOT_FOUND`, `PERMISSION_ERROR`
			""")
	@GetMapping("/sessions/{sessionId}")
	public ResponseEntity<?> getSessionAttendances(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long sessionId) {
		return ResponseEntity.ok(attendanceSessionManager.getSessionAttendances(userDetails.getUsername(), sessionId));
	}

	@Operation(
		summary = "[운영진] 특정 출석 레코드 상태 수정",
		description = """
			특정 출석 레코드의 상태를 출석/지각/결석으로 수정합니다. **운영진(ADMIN) 권한 필요.**

			**경로 변수**: `attendanceId` — 수정할 출석 레코드 ID
			**요청 본문**: `status` — `PRESENT`(출석) / `LATE`(지각) / `ABSENT`(결석)

			**주요 에러**: `PERMISSION_ERROR`
			""")
	@PatchMapping("/{attendanceId}/status")
	public ResponseEntity<?> updateAttendanceStatus(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long attendanceId,
		@Valid @RequestBody UpdateAttendanceStatusRequest request) {
		return ResponseEntity.ok(
			attendanceSessionManager.updateAttendanceStatus(userDetails.getUsername(), attendanceId, request.getStatus()));
	}

	@Operation(
		summary = "[운영진] 출석 세션(날짜) 수동 생성",
		description = """
			새 출석 세션(날짜)을 수동으로 생성합니다. **운영진(ADMIN) 권한 필요.**

			- 세션 생성 시 고유 토큰(UUID)이 발급되어 QR 위·변조 방지에 사용됩니다.
			- 요청 본문의 `sessionDate` 를 비우면 현재 시각으로 생성됩니다.

			**주요 에러**: `PERMISSION_ERROR`
			""")
	@PostMapping("/sessions")
	public ResponseEntity<?> createSession(@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody CreateAttendanceSessionRequest request) {
		return ResponseEntity.ok(
			attendanceSessionManager.createSession(userDetails.getUsername(), request.getSessionDate()));
	}

	@Operation(
		summary = "[운영진] 출석 세션(날짜) 삭제",
		description = """
			특정 출석 세션(날짜)을 삭제합니다. **운영진(ADMIN) 권한 필요.**

			- 세션에 연관된 출석 레코드도 함께 삭제됩니다.

			**경로 변수**: `sessionId` — 삭제할 세션 ID

			**주요 에러**: `SESSION_NOT_FOUND`, `PERMISSION_ERROR`
			""")
	@DeleteMapping("/sessions/{sessionId}")
	public ResponseEntity<?> deleteSession(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long sessionId) {
		attendanceSessionManager.deleteSession(userDetails.getUsername(), sessionId);
		return ResponseEntity.ok("세션이 삭제되었습니다.");
	}
}
