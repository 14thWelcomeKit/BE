package com.likelion13th.Welcomekit_BE.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetMyBingoResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.BingoEnum;
import com.likelion13th.Welcomekit_BE.service.BingoService;
import com.likelion13th.Welcomekit_BE.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BingoManager {
	@Autowired
	private final BingoService bingoService;
	@Autowired
	private final UserService userService;

	public List<GetMyBingoResponse> getMyBingo(String studentNum) {
		User user = userService.getUserByStudentNum(studentNum);
		return bingoService.getMyBingo(user);
	}

	public String revealBingoCell(String studentNum, Long id) {
		User user = userService.getUserByStudentNum(studentNum);
		return bingoService.revealBingoCell(user, id);
	}
}
