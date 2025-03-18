package com.likelion13th.Welcomekit_BE.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import com.likelion13th.Welcomekit_BE.domain.Bingo;
import com.likelion13th.Welcomekit_BE.domain.BingoCell;
import com.likelion13th.Welcomekit_BE.domain.Team;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateTeamRequest;
import com.likelion13th.Welcomekit_BE.domain.enums.BingoEnum;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamService {
	@Autowired
	private final TeamRepository teamRepository;

	@Transactional
	public void createTeam(CreateTeamRequest createTeamRequest) {
		// 1. 팀 생성
		Team team = Team.builder()
			.teamName(createTeamRequest.getTeamName())
			.build();
		teamRepository.save(team);

		Bingo bingo = Bingo.builder()
			.team(team)
			.cells(new ArrayList<>())
			.build();

		List<BingoEnum> missions = BingoEnum.getRandomMissions(9);
		List<BingoCell> cells = new ArrayList<>();

		for (BingoEnum mission : missions) {
			BingoCell cell = BingoCell.builder()
				.bingo(bingo)
				.mission(mission)
				.isComplete(false)
				.isRevealed(false)
				.build();
			cells.add(cell);
		}

		bingo.setCells(cells);
		team.setBingo(bingo);

		teamRepository.save(team);
	}

	public void setExecutives(User executive, String teamName) {
		Team team = teamRepository.findTeamByTeamName(teamName)
			.orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
		List<User> executives = team.getExecutives();
		executives.add(executive);
		team.setExecutives(executives);
		executive.setTeam(team);
		teamRepository.save(team);
	}

	public void setLeader(User leader, String teamName) {
		Team team = teamRepository.findTeamByTeamName(teamName)
			.orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
		team.setLeader(leader);
		leader.setTeam(team);
		teamRepository.save(team);
	}

	public Team getTeam(String teamName) {
		return teamRepository.findTeamByTeamName(teamName)
			.orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));
	}

	public void setTeamMember(List<User> userList, String teamName) {
		Team team = getTeam(teamName);
		userList.forEach(user -> team.getMembers().add(user));
		teamRepository.save(team);
	}
}
