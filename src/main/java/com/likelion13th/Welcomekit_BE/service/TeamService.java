package com.likelion13th.Welcomekit_BE.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.likelion13th.Welcomekit_BE.domain.Team;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateTeamRequest;
import com.likelion13th.Welcomekit_BE.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamService {
	@Autowired
	private final TeamRepository teamRepository;

	public void createTeam(CreateTeamRequest createTeamRequest) {
		Team team = new Team();
		team.setTeamName(createTeamRequest.getTeamName());
		teamRepository.save(team);
	}

	public void setExecutives(User executive, String teamName) {
		Team team = teamRepository.findTeamByTeamName(teamName)
			.orElseThrow(() -> new NotFoundException("해당 팀 명으로 존재하지 않습니다."));
		List<User> executives = team.getExecutives();
		executives.add(executive);
		team.setExecutives(executives);
		executive.setTeam(team);
		teamRepository.save(team);
	}

	public void setLeader(User leader, String teamName) {
		Team team = teamRepository.findTeamByTeamName(teamName)
			.orElseThrow(() -> new NotFoundException("해당 팀 명으로 존재하지 않습니다."));
		team.setLeader(leader);
		leader.setTeam(team);
		teamRepository.save(team);
	}

	public Team getTeam(String teamName) {
		return teamRepository.findTeamByTeamName(teamName)
			.orElseThrow(() -> new NotFoundException("해당 팀 명으로 존재하지 않습니다."));
	}

	public void setTeamMember(List<User> userList, String teamName) {
		Team team = getTeam(teamName);
		userList.forEach(user -> team.getMembers().add(user));
		teamRepository.save(team);
	}
}
