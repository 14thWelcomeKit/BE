package com.likelion13th.Welcomekit_BE.service;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.likelion13th.Welcomekit_BE.domain.Attendance;
import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetTodayAttendanceResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.MyAttendanceResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.AttendanceStatus;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.repository.AttendanceRepository;
import com.likelion13th.Welcomekit_BE.repository.AttendanceSessionRepository;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceSessionService {
	@Autowired
	private final AttendanceSessionRepository attendanceSessionRepository;
	@Autowired
	private final AttendanceRepository attendanceRepository;

	// 매주 새로운 출석 세션을 생성하는 메서드
	public AttendanceSession createNewSession() {
		AttendanceSession session = AttendanceSession.builder()
			.sessionDate(LocalDateTime.now()) // 현재 날짜로 출석 세션 생성
			.build();
		return attendanceSessionRepository.save(session);
	}

	public AttendanceSession createNewSession(List<User> totalBabyLion) {
		AttendanceSession session = AttendanceSession.builder()
			.sessionDate(LocalDateTime.now()) // 현재 날짜로 출석 세션 생성
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

	// 오늘 생성된 출석 세션이 있는지 확인
	public AttendanceSession getTodaySession() {
		return attendanceSessionRepository.findTopBySessionDateAfter(LocalDateTime.now().toLocalDate().atStartOfDay())
			.orElseGet(this::createNewSession); // 없으면 새로 생성
	}

	// 오늘 생성된 출석 세션이 있는지 확인
	public void getTodaySession(List<User> totalBabyLion) {
		attendanceSessionRepository.findTopBySessionDateAfter(LocalDateTime.now().toLocalDate().atStartOfDay())
			.orElseGet(() -> createNewSession(totalBabyLion));
	}

	public void generateQR(HttpServletResponse response) {
		String qrUrl = "https://welcomekitbe.lion.it.kr/api/attendance/success";

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

	public String markAttendance(User user) {
		AttendanceSession session = getTodaySession();

		Optional<Attendance> existingAttendance = attendanceRepository.findByUserAndAttendanceSession(user, session);
		if (existingAttendance.isPresent()) {
			if (existingAttendance.get().getStatus() == AttendanceStatus.PRESENT) {
				return "이미 출석한 기록이 있습니다.";
			}
		}

		// 출석 상태 결정 (지각 여부 판단 가능)
		AttendanceStatus status = LocalDateTime.now().isBefore(session.getSessionDate().plusMinutes(20))
			? AttendanceStatus.PRESENT
			: AttendanceStatus.LATE;

		Attendance attendance = attendanceRepository.findByUserAndAttendanceSession(user, session)
			.orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));
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
			throw new CustomException(ErrorCode.SESSION_NOT_FOUND);
		}
		return attendanceResponses;
	}
}
