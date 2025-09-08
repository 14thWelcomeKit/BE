package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.request.SelectManitoRequest;
import com.likelion13th.Welcomekit_BE.service.ManitoService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/manito")
public class ManitoController {

	private final ManitoService manitoService;

	@PostMapping("/create")
	@Operation(summary = "마니또 배정 생성")
	public ResponseEntity<?> createManito() {
		manitoService.createManitoAssignments();
		return ResponseEntity.ok().build();
	}

	@GetMapping("/my")
	@Operation(summary = "내 마니또 조회")
	public ResponseEntity<?> getMyManito(@AuthenticationPrincipal UserDetails userDetails) {
		String manito = manitoService.getManito(userDetails.getUsername());
		return ResponseEntity.ok(manito);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "마니또 전체 초기화(절대 막 누르지마시오!!) 대표, 프론트만 사용가능")
	public ResponseEntity<?> deleteManito(@AuthenticationPrincipal UserDetails userDetails) {
		manitoService.deleteManito(userDetails.getUsername());
		return ResponseEntity.ok().build();
	}

	@PostMapping("/select")
	@Operation(summary = "내 마니또(나를 챙겨주는 사람) 일거 같은 사람 선택")
	public ResponseEntity<?> selectManito(@AuthenticationPrincipal UserDetails userDetails, @RequestBody
	SelectManitoRequest selectManitoRequest) {
		manitoService.selectManito(userDetails.getUsername(), selectManitoRequest);
		return ResponseEntity.ok().build();
	}
}
