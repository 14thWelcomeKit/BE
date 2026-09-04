package com.likelion13th.Welcomekit_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.Attendance;
import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;
import com.likelion13th.Welcomekit_BE.domain.User;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	Optional<Attendance> findByUserAndAttendanceSession(User user, AttendanceSession attendanceSession);

	// 내 출석 내역: 세션 날짜 최신순 정렬 + 페이지네이션
	Page<Attendance> findByUserOrderByAttendanceSession_SessionDateDesc(User user, Pageable pageable);

	List<Attendance> findAllByAttendanceSession(AttendanceSession attendanceSession);
}
