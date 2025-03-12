package com.likelion13th.Welcomekit_BE.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateTeamRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.PutTeamExecutivesRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.PutTeamLeaderRequest;
import com.likelion13th.Welcomekit_BE.service.TeamService;
import com.likelion13th.Welcomekit_BE.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamManager {
	@Autowired
	private final TeamService teamService;
	@Autowired
	private final UserService userService;

	public void createTeam(CreateTeamRequest createTeamRequest) {
		teamService.createTeam(createTeamRequest);
	}

	public void setExecutives(PutTeamExecutivesRequest putTeamExecutivesRequest) {
		User executive = userService.getUserByStudentName(putTeamExecutivesRequest.getExecutiveStudentNum());
		executive.setTeam(teamService.getTeam(putTeamExecutivesRequest.getTeamName()));
		userService.save(executive);
		teamService.setExecutives(executive, putTeamExecutivesRequest.getTeamName());
	}

	public void setLeader(PutTeamLeaderRequest putTeamLeaderRequest) {
		User leader = userService.getUserByStudentName(putTeamLeaderRequest.getStudentNum());
		leader.setTeam(teamService.getTeam(putTeamLeaderRequest.getTeamName()));
		userService.save(leader);
		teamService.setLeader(leader, putTeamLeaderRequest.getTeamName());
	}

}
