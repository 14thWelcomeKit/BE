package com.likelion13th.Welcomekit_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.Attendance;
import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;
import com.likelion13th.Welcomekit_BE.domain.User;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
	Optional<Attendance> findByUserAndAttendanceSession(User user, AttendanceSession attendanceSession);
}
