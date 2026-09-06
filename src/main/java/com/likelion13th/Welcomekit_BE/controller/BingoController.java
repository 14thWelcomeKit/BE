package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.ApiResponse;
import com.likelion13th.Welcomekit_BE.exception.BingoException;
import com.likelion13th.Welcomekit_BE.manager.BingoManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 개인 빙고 API. 공통 Base URL({@code /api/v3/welcome-kit}) 이 자동으로 붙어
 * 실제 매핑은 {@code /api/v3/welcome-kit/bingo} 이다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/bingo")
@Tag(name = "빙고", description = "개인 빙고판 조회 API")
public class BingoController {

	private final BingoManager bingoManager;

	@Operation(summary = "빙고판 전체 조회", description = "5x5 빙고판의 전체 상태와 본인 코드를 조회합니다.",
		security = @SecurityRequirement(name = "Bearer Authentication"))
	@GetMapping("")
	public ResponseEntity<?> getBingoBoard(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(
			ApiResponse.success("S200", "빙고판 조회에 성공했습니다", bingoManager.getBingoBoard(userDetails.getUsername())));
	}

	/** 매칭 규칙 위반 등 DTO 검증만으로 표현 못 하는 빙고 비즈니스 규칙 위반. */
	@ExceptionHandler(BingoException.class)
	public ResponseEntity<ApiResponse<Object>> handleBingoException(BingoException ex) {
		return ResponseEntity.status(ex.getHttpStatus())
			.body(ApiResponse.error(ex.getCode(), ex.getMessage()));
	}

	/** 빙고 처리 중 예상치 못한 오류: 명세상 500 / E500 형태로 응답한다. */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleBingoError(Exception ex) {
		log.error("[빙고] 처리 실패", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error("E500", "빙고판 조회 중 오류가 발생했습니다"));
	}
}
