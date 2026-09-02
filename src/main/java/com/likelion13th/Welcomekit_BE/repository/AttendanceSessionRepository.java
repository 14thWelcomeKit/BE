package com.likelion13th.Welcomekit_BE.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;

public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
	Optional<AttendanceSession> findTopBySessionDateAfter(LocalDateTime date);

	java.util.List<AttendanceSession> findAllByOrderBySessionDateDesc();

	boolean existsBySessionDateBetween(LocalDateTime start, LocalDateTime end);
}
