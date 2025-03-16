package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.manager.UserManager;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
public class UserController {

	@Autowired
	private final UserManager userManager;

	@PostMapping("/join")
	ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
		userManager.createUser(createUserRequest);
		return ResponseEntity.ok("성공적으로 생성했습니다!");
	}

	@GetMapping("/total/baby_lion")
	ResponseEntity<?> getAllBabyLion(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(userManager.getTotalBabyLion(userDetails));
	}

	@GetMapping("/total/admin")
	ResponseEntity<?> getAllAdmin(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(userManager.getTotalAdmin(userDetails));
	}

	@GetMapping("/info")
	ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(userManager.getMyInfo(userDetails));
	}
}
