package com.likelion13th.Welcomekit_BE.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoBoardResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoVerifyResponse;
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

	public BingoBoardResponse getBingoBoard(String email) {
		User user = userService.getUserByEmail(email);
		return bingoService.getBingoBoard(user);
	}

	public BingoVerifyResponse verifyCell(String email, Integer cellId, String opponentCode) {
		User user = userService.getUserByEmail(email);
		return bingoService.verifyCell(user, cellId, opponentCode);
	}
}
