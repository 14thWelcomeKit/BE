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
import com.likelion13th.Welcomekit_BE.domain.dto.request.PutTeamMemberRequest;
import com.likelion13th.Welcomekit_BE.manager.TeamManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
@Tag(name = "팀", description = "팀 생성 및 운영진/팀장/팀원 등록 API.")
public class TeamController {
	@Autowired
	private final TeamManager teamManager;

	@Operation(summary = "팀 생성", description = "새 팀을 생성합니다.")
	@PostMapping("/create")
	ResponseEntity<?> createTeam(@RequestBody CreateTeamRequest createTeamRequest) {
		teamManager.createTeam(createTeamRequest);
		return ResponseEntity.ok("성공적으로 팀이 생성 되었습니다.");
	}

	@Operation(summary = "팀 운영진 등록", description = "지정한 팀에 운영진을 등록합니다.")
	@PostMapping("/executives")
	ResponseEntity<?> setExecutives(@RequestBody PutTeamExecutivesRequest putTeamExecutivesRequest) {
		teamManager.setExecutives(putTeamExecutivesRequest);
		return ResponseEntity.ok("성공적으로 운영자가 등록되었습니다.");
	}

	@Operation(summary = "팀장 등록", description = "지정한 팀의 팀장을 등록합니다.")
	@PostMapping("/leader")
	ResponseEntity<?> setLeader(@RequestBody PutTeamLeaderRequest putTeamLeaderRequest) {
		teamManager.setLeader(putTeamLeaderRequest);
		return ResponseEntity.ok("성공적으로 팀장이 등록되었습니다.");
	}

	@Operation(summary = "팀원 등록", description = "지정한 팀에 팀원을 등록합니다.")
	@PostMapping("/teamMember")
	ResponseEntity<?> setTeamMember(@RequestBody PutTeamMemberRequest putTeamMemberRequest) {
		teamManager.setTeamMember(putTeamMemberRequest);
		return ResponseEntity.ok("성공적으로 팀원이 등록되었습니다.");
	}
}
