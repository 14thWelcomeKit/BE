package com.likelion13th.Welcomekit_BE.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
	Optional<AttendanceSession> findTopBySessionDateAfter(LocalDateTime date);

	// 지정한 기간(오늘 0시 ~ 내일 0시) 내 세션 중 가장 최근 것 하나.
	// 오늘 세션이 여러 개여도 최신 1개만 대표로 조회한다.
	Optional<AttendanceSession> findTopBySessionDateBetweenOrderBySessionDateDesc(
		LocalDateTime start, LocalDateTime end);

	java.util.List<AttendanceSession> findAllByOrderBySessionDateDesc();

	boolean existsBySessionDateBetween(LocalDateTime start, LocalDateTime end);
}
