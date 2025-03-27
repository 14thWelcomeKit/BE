package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13th.Welcomekit_BE.domain.dto.request.ChangePasswordRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.manager.UserManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

	@PostMapping("/password")
	ResponseEntity<?> changePassword(@AuthenticationPrincipal UserDetails userDetails, @RequestBody
	ChangePasswordRequest changePasswordRequest
	) {
		userManager.changePassword(userDetails, changePasswordRequest.getCurrentPassword(),
			changePasswordRequest.getNewPassword());
		return ResponseEntity.ok("password changed");
	}

	@Operation(summary = "프로필 이미지 업로드", description = "multipart로 프로필 이미지를 업로드합니다.")
	@PostMapping(value = "/uploadProfile", consumes = "multipart/form-data")
	ResponseEntity<?> uploadProfile(@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(name = "file", description = "업로드 사진 데이터")
		@RequestParam(value = "file") MultipartFile file) {
		userManager.saveProfileImage(file, userDetails);
		return ResponseEntity.ok("저장 완료");
	}

	@Operation(summary = "프로필 이미지 조회", description = "multipart로 프로필 이미지를 조회합니다.")
	@GetMapping("/profileImage")
	public ResponseEntity<Resource> getProfileImage(@AuthenticationPrincipal UserDetails userDetails) {

		Resource image = userManager.getProfileImage(userDetails);
		String contentType = userManager.getProfileSource(image);

		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(contentType))
			.body(image);
	}
}
