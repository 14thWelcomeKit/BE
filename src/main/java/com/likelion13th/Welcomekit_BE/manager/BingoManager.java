package com.likelion13th.Welcomekit_BE.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.Team;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetMyBingoResponse;
import com.likelion13th.Welcomekit_BE.service.BingoService;
import com.likelion13th.Welcomekit_BE.service.TeamService;
import com.likelion13th.Welcomekit_BE.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BingoManager {
	@Autowired
	private final BingoService bingoService;
	@Autowired
	private final UserService userService;
	@Autowired
	private final TeamService teamService;

	public List<GetMyBingoResponse> getMyBingo(String email) {
		User user = userService.getUserByEmail(email);
		return bingoService.getMyBingo(user);
	}

	public String revealBingoCell(String email, Long id) {
		User user = userService.getUserByEmail(email);
		return bingoService.revealBingoCell(user, id);
	}

	public void approveTeam(UserDetails userDetails, String teamName) {
		Team team = teamService.getTeam(teamName);
		User user = userService.getUserByEmail(userDetails.getUsername());
		bingoService.approveTeam(user, team);
	}
}
