package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.manager.BingoManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bingo")
@Tag(name = "빙고", description = "개인 빙고 보드 조회 및 셀 공개·승인 API.")
public class BingoController {

	@Autowired
	private final BingoManager bingoManager;

	@Operation(summary = "내 빙고 보드 조회", description = "로그인한 사용자의 빙고 보드를 조회합니다.")
	@GetMapping("")
	ResponseEntity<?> getMyBingo(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(bingoManager.getMyBingo(userDetails.getUsername()));
	}

	@Operation(summary = "빙고 셀 공개", description = "지정한 빙고 셀을 공개(reveal) 처리합니다.")
	@PutMapping("/reveal/{bingo_cell_id}")
	ResponseEntity<?> revealBingoCell(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long bingo_cell_id) {
		return ResponseEntity.ok(bingoManager.revealBingoCell(userDetails.getUsername(), bingo_cell_id));
	}

	@Operation(summary = "팀 승인", description = "지정한 팀명을 승인 처리합니다.")
	@PutMapping("approve/{team_name}")
	ResponseEntity<?> approveTeam(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String team_name) {
		bingoManager.approveTeam(userDetails, team_name);
		return ResponseEntity.ok("success");
	}
}
