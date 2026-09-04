package com.likelion13th.Welcomekit_BE.manager;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.AttendanceDetailResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.AttendanceSessionSummaryResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetTodayAttendanceResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.MyAttendanceResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.AttendanceStatus;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.service.AttendanceSessionService;
import com.likelion13th.Welcomekit_BE.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceSessionManager {

	@Autowired
	private final AttendanceSessionService attendanceSessionService;
	@Autowired
	private final UserService userService;

	public void generateQR(String email, HttpServletResponse response) {
		User user = userService.getUserByEmail(email);
		if (user.getUserType() == UserType.BABY_LION) {
			log.error("QR 생성할떄 permission error");
			throw new CustomException(ErrorCode.PERMISSION_ERROR);
		}
		List<User> totalBabyLion = userService.getTotalBabyLionUser();
		AttendanceSession session = attendanceSessionService.getTodaySession(totalBabyLion);
		attendanceSessionService.generateQR(response, session.getToken());
	}

	public String markAttendance(String email, String token) {
		User user = userService.getUserByEmail(email);
		return attendanceSessionService.markAttendance(user, token);
	}

	public Page<MyAttendanceResponse> getMyAttendance(String email, Pageable pageable) {
		User user = userService.getUserByEmail(email);
		return attendanceSessionService.getMyAttendance(user, pageable);
	}

	public List<GetTodayAttendanceResponse> getTodayAttendance(String email) {
		// 로그인 사용자 확인(인증 목적). 조회는 오늘 세션의 전체 출석 현황을 반환한다.
		userService.getUserByEmail(email);
		List<User> totalBabyLion = userService.getTotalBabyLionUser();
		return attendanceSessionService.getTodayAttendance(totalBabyLion);
	}

	// ══════════════════════════════════════════════════════════
	// 운영진(ADMIN) 전용 출석 관리 (C-1 + C-2)
	// ══════════════════════════════════════════════════════════

	public List<AttendanceSessionSummaryResponse> getAllSessions(String email) {
		requireAdmin(email);
		return attendanceSessionService.getAllSessions();
	}

	public List<AttendanceDetailResponse> getSessionAttendances(String email, Long sessionId) {
		requireAdmin(email);
		List<User> totalBabyLion = userService.getTotalBabyLionUser();
		return attendanceSessionService.getSessionAttendances(sessionId, totalBabyLion);
	}

	public AttendanceDetailResponse updateAttendanceStatus(String email, Long attendanceId, AttendanceStatus status) {
		requireAdmin(email);
		return attendanceSessionService.updateAttendanceStatus(attendanceId, status);
	}

	public AttendanceSessionSummaryResponse createSession(String email, LocalDateTime sessionDate) {
		requireAdmin(email);
		List<User> totalBabyLion = userService.getTotalBabyLionUser();
		return attendanceSessionService.createSession(sessionDate, totalBabyLion);
	}

	public void deleteSession(String email, Long sessionId) {
		requireAdmin(email);
		attendanceSessionService.deleteSession(sessionId);
	}

	private User requireAdmin(String email) {
		User user = userService.getUserByEmail(email);
		if (user.getUserType() != UserType.ADMIN) {
			log.error("출석 관리 권한 없음: {}", email);
			throw new CustomException(ErrorCode.PERMISSION_ERROR);
		}
		return user;
	}
}
