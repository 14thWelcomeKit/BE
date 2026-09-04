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

	List<Attendance> findAllByAttendanceSession(AttendanceSession attendanceSession);

	// 오늘 세션의 전체 출석 현황(운영진 출석부용).
	// 팀이 배정되지 않은 사용자도 포함되도록 team은 LEFT JOIN 한다(팀 없으면 teamName=null).
	@Query("SELECT new com.likelion13th.Welcomekit_BE.domain.dto.response.GetTodayAttendanceResponse(" +
		"t.teamName, u.userName, a.status) " +
		"FROM attendance a " +
		"JOIN a.user u " +
		"LEFT JOIN u.team t " +
		"WHERE a.attendanceSession.sessionDate > :date " +
		"ORDER BY t.id")
	List<GetTodayAttendanceResponse> findTodayAttendance(@Param("date") LocalDateTime date);
}
