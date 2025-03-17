package com.likelion13th.Welcomekit_BE.manager;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateTeamRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.PutTeamExecutivesRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.PutTeamLeaderRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.PutTeamMemberRequest;
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
		User executive = userService.getUserByStudentNum(putTeamExecutivesRequest.getExecutiveStudentNum());
		executive.setTeam(teamService.getTeam(putTeamExecutivesRequest.getTeamName()));
		userService.save(executive);
		teamService.setExecutives(executive, putTeamExecutivesRequest.getTeamName());
	}

	public void setLeader(PutTeamLeaderRequest putTeamLeaderRequest) {
		User leader = userService.getUserByStudentNum(putTeamLeaderRequest.getStudentNum());
		leader.setTeam(teamService.getTeam(putTeamLeaderRequest.getTeamName()));
		userService.save(leader);
		teamService.setLeader(leader, putTeamLeaderRequest.getTeamName());
	}

	public void setTeamMember(PutTeamMemberRequest putTeamMemberRequest) {
		List<User> userList = new ArrayList<>();
		putTeamMemberRequest.getExecutiveStudentNumList().forEach(studentNum -> {
			User user = userService.getUserByStudentNum(studentNum);
			user.setTeam(teamService.getTeam(putTeamMemberRequest.getTeamName()));
			userService.save(user);
			userList.add(userService.getUserByStudentNum(studentNum));
		});
		teamService.setTeamMember(userList, putTeamMemberRequest.getTeamName());
	}

}
