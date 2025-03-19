package com.likelion13th.Welcomekit_BE.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetTodayAttendanceResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.MyAttendanceResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
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

	public void generateQR(String studentNum, HttpServletResponse response) {
		User user = userService.getUserByStudentNum(studentNum);
		if (user.getUserType() == UserType.BABY_LION) {
			throw new CustomException(ErrorCode.PERMISSION_ERROR);
		}
		List<User> totalBabyLion = userService.getTotalBabyLionUser();
		attendanceSessionService.getTodaySession(totalBabyLion);
		attendanceSessionService.generateQR(response);
	}

	public String markAttendance(String studentNum) {
		User user = userService.getUserByStudentNum(studentNum);
		return attendanceSessionService.markAttendance(user);
	}

	public List<MyAttendanceResponse> getMyAttendance(String studentNum) {
		User user = userService.getUserByStudentNum(studentNum);
		return attendanceSessionService.getMyAttendance(user);
	}

	public List<GetTodayAttendanceResponse> getTodayAttendance(String studentNum) {
		User user = userService.getUserByStudentNum(studentNum);
		return attendanceSessionService.getTodayAttendance(user);
	}
}
