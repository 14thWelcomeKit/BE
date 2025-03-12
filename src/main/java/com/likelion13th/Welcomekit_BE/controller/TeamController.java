package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateTeamRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.PutTeamExecutivesRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.PutTeamLeaderRequest;
import com.likelion13th.Welcomekit_BE.manager.TeamManager;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/team")
public class TeamController {
	@Autowired
	private final TeamManager teamManager;

	@PostMapping("/create")
	ResponseEntity<?> createTeam(@RequestBody CreateTeamRequest createTeamRequest) {
		teamManager.createTeam(createTeamRequest);
		return ResponseEntity.ok("성공적으로 팀이 생성 되었습니다.");
	}

	@PostMapping("/executives")
	ResponseEntity<?> setExecutives(@RequestBody PutTeamExecutivesRequest putTeamExecutivesRequest) {
		teamManager.setExecutives(putTeamExecutivesRequest);
		return ResponseEntity.ok("성공적으로 운영자가 등록되었습니다.");
	}

	@PostMapping("/leader")
	ResponseEntity<?> setLeader(@RequestBody PutTeamLeaderRequest putTeamLeaderRequest) {
		teamManager.setLeader(putTeamLeaderRequest);
		return ResponseEntity.ok("성공적으로 팀장이 등록되었습니다.");
	}
}
