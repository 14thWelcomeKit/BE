package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13th.Welcomekit_BE.domain.dto.request.ChangePasswordRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.PromoteAdminRequest;
import com.likelion13th.Welcomekit_BE.manager.UserManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "회원", description = "회원가입, 회원 목록/내 정보 조회, 비밀번호 변경, 운영진 승격, 프로필 이미지 API.")
public class UserController {

	@Autowired
	private final UserManager userManager;

	@Operation(summary = "회원가입", description = "이메일 인증 완료 후 회원가입합니다. 권한(userType)은 클라이언트가 지정하지 않으며, 초대코드 일치 시 ADMIN, 아니면 BABY_LION으로 서버가 결정합니다.")
	@PostMapping("/join")
	ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
		userManager.createUser(createUserRequest);
		return ResponseEntity.ok("성공적으로 생성했습니다!");
	}

	@Operation(summary = "전체 아기사자(BABY_LION) 목록 조회", description = "일반 회원(BABY_LION) 전체 목록을 조회합니다.")
	@GetMapping("/total/baby_lion")
	ResponseEntity<?> getAllBabyLion(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(userManager.getTotalBabyLion(userDetails));
	}

	@Operation(summary = "나를 제외한 전체 회원 목록 조회", description = "로그인한 본인을 제외한 전체 회원 목록을 조회합니다.")
	@GetMapping("/total/exceptMe")
	ResponseEntity<?> getAllExceptMe(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(userManager.getTotalExceptMe(userDetails));
	}

	@Operation(summary = "전체 운영진(ADMIN) 목록 조회", description = "운영진(ADMIN) 전체 목록을 조회합니다.")
	@GetMapping("/total/admin")
	ResponseEntity<?> getAllAdmin(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(userManager.getTotalAdmin(userDetails));
	}

	@Operation(summary = "운영진 승격", description = "운영진(ADMIN)만 특정 유저를 운영진으로 승격할 수 있습니다.")
	@PostMapping("/promote")
	ResponseEntity<?> promoteToAdmin(@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody PromoteAdminRequest request) {
		userManager.promoteToAdmin(userDetails, request.getTargetUserId());
		return ResponseEntity.ok("운영진으로 승격되었습니다.");
	}

	@Operation(summary = "내 정보 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
	@GetMapping("/info")
	ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(userManager.getMyInfo(userDetails));
	}

	@Operation(summary = "비밀번호 변경(마이페이지)", description = "로그인 상태에서 현재 비밀번호를 확인한 뒤 새 비밀번호로 변경합니다. (비밀번호 재설정/찾기와는 별개)")
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
