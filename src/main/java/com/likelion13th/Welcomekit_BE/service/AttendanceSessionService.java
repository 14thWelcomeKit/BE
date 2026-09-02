package com.likelion13th.Welcomekit_BE.service;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.likelion13th.Welcomekit_BE.domain.Attendance;
import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.AttendanceDetailResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.AttendanceSessionSummaryResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetTodayAttendanceResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.MyAttendanceResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.AttendanceStatus;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.repository.AttendanceRepository;
import com.likelion13th.Welcomekit_BE.repository.AttendanceSessionRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceSessionService {
	@Autowired
	private final AttendanceSessionRepository attendanceSessionRepository;
	@Autowired
	private final AttendanceRepository attendanceRepository;

	// QR용 베이스 URL(환경에 따라 다르면 설정값으로 주입)
	@Value("${app.attendance.qr-base-url:https://welcomekitbe.lion.it.kr/api/attendance/success}")
	private String qrBaseUrl;

	// 세션 생성 시 발급되는 토큰 생성기
	private String generateSessionToken() {
		return java.util.UUID.randomUUID().toString().replace("-", "");
	}

	// 매주 새로운 출석 세션을 생성하는 메서드
	public AttendanceSession createNewSession() {
		AttendanceSession session = AttendanceSession.builder()
			.sessionDate(LocalDateTime.now()) // 현재 날짜로 출석 세션 생성
			.token(generateSessionToken())
			.build();
		return attendanceSessionRepository.save(session);
	}

	public AttendanceSession createNewSession(List<User> totalBabyLion) {
		AttendanceSession session = AttendanceSession.builder()
			.sessionDate(LocalDateTime.now()) // 현재 날짜로 출석 세션 생성
			.token(generateSessionToken())
			.build();
		AttendanceSession save = attendanceSessionRepository.save(session);
		totalBabyLion.forEach(babyLion -> {
			Attendance attendance = new Attendance();
			attendance.setAttendanceSession(save);
			attendance.setUser(babyLion);
			attendance.setStatus(AttendanceStatus.ABSENT);
			attendanceRepository.save(attendance);
		});
		return save;
	}

	// 오늘 생성된 출석 세션이 있는지 확인(없으면 생성). 토큰이 없던 기존 세션이면 토큰을 채워준다.
	public AttendanceSession getTodaySession() {
		AttendanceSession session = attendanceSessionRepository
			.findTopBySessionDateAfter(LocalDateTime.now().toLocalDate().atStartOfDay())
			.orElseGet(this::createNewSession);
		return ensureToken(session);
	}

	// 오늘 생성된 출석 세션이 있는지 확인(없으면 전원 출석 칸과 함께 생성)하고 세션을 반환한다.
	public AttendanceSession getTodaySession(List<User> totalBabyLion) {
		AttendanceSession session = attendanceSessionRepository
			.findTopBySessionDateAfter(LocalDateTime.now().toLocalDate().atStartOfDay())
			.orElseGet(() -> createNewSession(totalBabyLion));
		return ensureToken(session);
	}

	private AttendanceSession ensureToken(AttendanceSession session) {
		if (session.getToken() == null || session.getToken().isBlank()) {
			session.setToken(generateSessionToken());
			attendanceSessionRepository.save(session);
		}
		return session;
	}

	/**
	 * QR 생성: 오늘 세션의 토큰을 QR URL에 심어, 그날 세션에만 유효한 QR을 만든다.
	 * (과거에 캡처해둔 QR은 다음 날 세션 토큰과 달라 사용 불가)
	 */
	public void generateQR(HttpServletResponse response, String sessionToken) {
		String qrUrl = qrBaseUrl + "?token=" + sessionToken;

		int width = 300;
		int height = 300;

		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(qrUrl, BarcodeFormat.QR_CODE, width, height);
			BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

			response.setContentType("image/png");
			OutputStream outputStream = response.getOutputStream();
			ImageIO.write(qrImage, "png", outputStream);

			outputStream.flush();
			outputStream.close();

		} catch (Exception e) {
			throw new RuntimeException("QR 코드 생성 중 오류 발생", e);
		}
	}

	@Transactional
	public String markAttendance(User user, String token) {
		AttendanceSession session = getTodaySession();

		// QR 토큰 검증: 오늘 세션 토큰과 일치해야 함(과거 QR/위조 QR 방지).
		// token이 null이면(구버전 클라이언트 호환) 검증을 건너뛰되, 값이 있으면 반드시 일치해야 한다.
		if (token != null && !token.isBlank()) {
			if (session.getToken() == null || !session.getToken().equals(token)) {
				log.error("QR 토큰 불일치로 출석 거부: user={}", user.getUserName());
				throw new CustomException(ErrorCode.INVALID_QR_TOKEN);
			}
		}

		// 개인 출석 칸이 없으면 그 자리에서 생성(QR 없이/세션 자동생성 상황 대응)
		Attendance attendance = attendanceRepository.findByUserAndAttendanceSession(user, session)
			.orElseGet(() -> {
				Attendance created = new Attendance();
				created.setUser(user);
				created.setAttendanceSession(session);
				created.setStatus(AttendanceStatus.ABSENT);
				return created;
			});

		if (attendance.getStatus() == AttendanceStatus.PRESENT) {
			return "이미 출석한 기록이 있습니다.";
		}

		// 출석 상태 결정 (지각 여부 판단 가능)
		AttendanceStatus status = LocalDateTime.now().isBefore(session.getSessionDate().plusMinutes(20))
			? AttendanceStatus.PRESENT
			: AttendanceStatus.LATE;

		attendance.setAttendanceTime(LocalDateTime.now());
		attendance.setStatus(status);
		attendanceRepository.save(attendance);

		return user.getUserName() + "님, " + (status == AttendanceStatus.PRESENT ? "출석 완료" : "지각 처리되었습니다.");
	}

	public List<MyAttendanceResponse> getMyAttendance(User user) {
		List<Attendance> myAttendances = attendanceRepository.findAllByUserOrderByAttendanceTime(user);
		List<MyAttendanceResponse> list = myAttendances.stream().map(myAttendance -> {
			MyAttendanceResponse myAttendanceResponse = new MyAttendanceResponse();
			myAttendanceResponse.setAttendanceStatus(myAttendance.getStatus());
			myAttendanceResponse.setDate(myAttendance.getAttendanceSession().getSessionDate().toLocalDate());
			return myAttendanceResponse;
		}).toList();
		return list;
	}

	public List<GetTodayAttendanceResponse> getTodayAttendance(User user) {
		List<GetTodayAttendanceResponse> attendanceResponses =
			attendanceRepository.findTodayAttendance(LocalDateTime.now().toLocalDate().atStartOfDay());
		if (attendanceResponses.isEmpty()) {
			log.error("세션이 없습니다.");
			throw new CustomException(ErrorCode.SESSION_NOT_FOUND);
		}
		return attendanceResponses;
	}

	// ══════════════════════════════════════════════════════════
	// 운영진(ADMIN) 전용 출석 관리 (C-1 + C-2)
	// ══════════════════════════════════════════════════════════

	/** 전체 출석 세션(날짜) 목록 조회 — 최신 날짜 순, 상태별 집계 포함 */
	public List<AttendanceSessionSummaryResponse> getAllSessions() {
		return attendanceSessionRepository.findAllByOrderBySessionDateDesc().stream()
			.map(session -> {
				List<Attendance> list = attendanceRepository.findAllByAttendanceSession(session);
				long present = list.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
				long late = list.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
				long absent = list.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
				return AttendanceSessionSummaryResponse.builder()
					.sessionId(session.getId())
					.sessionDate(session.getSessionDate())
					.totalCount(list.size())
					.presentCount(present)
					.lateCount(late)
					.absentCount(absent)
					.build();
			})
			.toList();
	}

	/** 특정 세션의 출석 상세(사람별) 조회 */
	public List<AttendanceDetailResponse> getSessionAttendances(Long sessionId) {
		AttendanceSession session = attendanceSessionRepository.findById(sessionId)
			.orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
		return attendanceRepository.findAllByAttendanceSession(session).stream()
			.map(this::toDetail)
			.toList();
	}

	/** 특정 출석 레코드의 상태 수정(출석/지각/결석) */
	@Transactional
	public AttendanceDetailResponse updateAttendanceStatus(Long attendanceId, AttendanceStatus status) {
		Attendance attendance = attendanceRepository.findById(attendanceId)
			.orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
		attendance.setStatus(status);
		// 결석으로 되돌리면 출석 시각 제거, 출석/지각으로 바꾸면 시각이 없을 때 현재 시각 기록
		if (status == AttendanceStatus.ABSENT) {
			attendance.setAttendanceTime(null);
		} else if (attendance.getAttendanceTime() == null) {
			attendance.setAttendanceTime(LocalDateTime.now());
		}
		attendanceRepository.save(attendance);
		return toDetail(attendance);
	}

	/**
	 * 세션(날짜) 수동 생성. 전달된 아기사자 전원에게 결석(ABSENT) 레코드를 생성한다.
	 * sessionDate가 null이면 현재 시각으로 생성한다.
	 */
	@Transactional
	public AttendanceSessionSummaryResponse createSession(LocalDateTime sessionDate, List<User> totalBabyLion) {
		LocalDateTime date = sessionDate != null ? sessionDate : LocalDateTime.now();
		AttendanceSession session = AttendanceSession.builder()
			.sessionDate(date)
			.token(generateSessionToken())
			.build();
		AttendanceSession saved = attendanceSessionRepository.save(session);

		totalBabyLion.forEach(babyLion -> {
			Attendance attendance = new Attendance();
			attendance.setAttendanceSession(saved);
			attendance.setUser(babyLion);
			attendance.setStatus(AttendanceStatus.ABSENT);
			attendanceRepository.save(attendance);
		});

		return AttendanceSessionSummaryResponse.builder()
			.sessionId(saved.getId())
			.sessionDate(saved.getSessionDate())
			.totalCount(totalBabyLion.size())
			.presentCount(0)
			.lateCount(0)
			.absentCount(totalBabyLion.size())
			.build();
	}

	/** 세션 삭제. 연관 출석 레코드도 cascade/orphanRemoval로 함께 삭제된다. */
	@Transactional
	public void deleteSession(Long sessionId) {
		AttendanceSession session = attendanceSessionRepository.findById(sessionId)
			.orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
		// 세션 하위 출석 레코드 명시적 삭제(연관 관계 매핑에 의존하지 않도록 안전하게 처리)
		attendanceRepository.deleteAll(attendanceRepository.findAllByAttendanceSession(session));
		attendanceSessionRepository.delete(session);
	}

	private AttendanceDetailResponse toDetail(Attendance attendance) {
		User u = attendance.getUser();
		return AttendanceDetailResponse.builder()
			.attendanceId(attendance.getId())
			.userId(u.getId())
			.name(u.getUserName())
			.studentNum(u.getStudentNum())
			.teamName(u.getTeam() != null ? u.getTeam().getTeamName() : null)
			.status(attendance.getStatus())
			.attendanceTime(attendance.getAttendanceTime())
			.build();
	}
}
