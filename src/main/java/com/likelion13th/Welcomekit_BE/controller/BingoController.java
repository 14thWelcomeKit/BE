package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.manager.BingoManager;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/bingo")
public class BingoController {

	@Autowired
	private final BingoManager bingoManager;

	@GetMapping("")
	ResponseEntity<?> getMyBingo(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(bingoManager.getMyBingo(userDetails.getUsername()));
	}

	@PutMapping("/reveal/{bingo_cell_id}")
	ResponseEntity<?> revealBingoCell(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long bingo_cell_id) {
		return ResponseEntity.ok(bingoManager.revealBingoCell(userDetails.getUsername(), bingo_cell_id));
	}
}
