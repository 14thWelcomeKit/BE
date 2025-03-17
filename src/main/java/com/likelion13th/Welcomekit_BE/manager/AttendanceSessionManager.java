package com.likelion13th.Welcomekit_BE.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.AttendanceSession;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetTodayAttendanceResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.MyAttendanceResponse;
import com.likelion13th.Welcomekit_BE.service.AttendanceSessionService;
import com.likelion13th.Welcomekit_BE.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceSessionManager {

	@Autowired
	private final AttendanceSessionService attendanceSessionService;
	@Autowired
	private final UserService userService;

	public void generateQR(HttpServletResponse response) {
		List<User> totalBabyLion = userService.getTotalBabyLionUser();
		AttendanceSession session = attendanceSessionService.getTodaySession(totalBabyLion);
		attendanceSessionService.generateQR(response, session.getId());
	}

	public String markAttendance(String studentNum) {
		User user = userService.getUserByStudentName(studentNum);
		return attendanceSessionService.markAttendance(user);
	}

	public List<MyAttendanceResponse> getMyAttendance(String studentNum) {
		User user = userService.getUserByStudentName(studentNum);
		return attendanceSessionService.getMyAttendance(user);
	}

	public List<GetTodayAttendanceResponse> getTodayAttendance(String studentNum) {
		User user = userService.getUserByStudentName(studentNum);
		return attendanceSessionService.getTodayAttendance(user);
	}
}
