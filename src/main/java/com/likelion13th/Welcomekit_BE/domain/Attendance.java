package com.likelion13th.Welcomekit_BE.domain;

import java.time.LocalDateTime;

import com.likelion13th.Welcomekit_BE.domain.enums.AttendanceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "attendance")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attendance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;  // 출석한 학생

	@ManyToOne
	@JoinColumn(name = "session_id", nullable = false)
	private AttendanceSession attendanceSession; // 새로운 출석 세션과 연결

	@Column(name = "attendance_time", nullable = false)
	private LocalDateTime attendanceTime; // 출석 시간

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private AttendanceStatus status;  // 출석 상태 (출석 / 지각 / 결석)
}
