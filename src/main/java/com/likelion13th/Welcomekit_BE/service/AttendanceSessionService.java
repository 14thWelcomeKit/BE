package com.likelion13th.Welcomekit_BE.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;
import com.likelion13th.Welcomekit_BE.repository.AttendanceSessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceSessionService {
	@Autowired
	private final AttendanceSessionRepository attendanceSessionRepository;

	// 매주 새로운 출석 세션을 생성하는 메서드
	public AttendanceSession createNewSession() {
		AttendanceSession session = AttendanceSession.builder()
			.sessionDate(LocalDateTime.now()) // 현재 날짜로 출석 세션 생성
			.build();
		return attendanceSessionRepository.save(session);
	}

	// 오늘 생성된 출석 세션이 있는지 확인
	public AttendanceSession getTodaySession() {
		return attendanceSessionRepository.findTopBySessionDateAfter(LocalDateTime.now().toLocalDate().atStartOfDay())
			.orElseGet(this::createNewSession); // 없으면 새로 생성
	}
}
