package com.likelion13th.Welcomekit_BE.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion13th.Welcomekit_BE.domain.Attendance;
import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetTodayAttendanceResponse;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	Optional<Attendance> findByUserAndAttendanceSession(User user, AttendanceSession attendanceSession);

	List<Attendance> findAllByUserOrderByAttendanceTime(User user);

	@Query("SELECT new com.likelion13th.Welcomekit_BE.domain.dto.response.GetTodayAttendanceResponse(" +
		"u.userName, t.teamName, a.status) " +
		"FROM attendance a " +
		"JOIN a.user u " +
		"JOIN u.team t " +
		"WHERE a.attendanceSession.sessionDate > :date")
	List<GetTodayAttendanceResponse> findTodayAttendance(@Param("date") LocalDateTime date);
}
