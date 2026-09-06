package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.ApiResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.request.BingoVerifyRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoVerifyResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoRankingResponse;
import com.likelion13th.Welcomekit_BE.exception.BingoException;
import com.likelion13th.Welcomekit_BE.manager.BingoManager;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
@Tag(name = "빙고", description = "개인 빙고판 조회 및 미션 인증(코드 상호 매칭) API")
public class BingoController {

	private final BingoManager bingoManager;

	@Operation(summary = "빙고판 전체 조회", description = "5x5 빙고판의 전체 상태와 본인 코드를 조회합니다.",
		security = @SecurityRequirement(name = "Bearer Authentication"))
	@GetMapping("")
	public ResponseEntity<?> getBingoBoard(@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(
			ApiResponse.success("S200", "빙고판 조회에 성공했습니다", bingoManager.getBingoBoard(userDetails.getUsername())));
	}

	@Operation(summary = "미션 인증(코드 상호 매칭)", description = "빙고 칸에 상대방의 개인 코드를 입력해 매칭을 시도합니다.",
		security = @SecurityRequirement(name = "Bearer Authentication"))
	@PostMapping("/cells/{cellId}/verify")
	public ResponseEntity<?> verifyCell(@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Integer cellId,
		@RequestBody BingoVerifyRequest request) {
		BingoVerifyResponse response = bingoManager.verifyCell(userDetails.getUsername(), cellId,
			request.getOpponentCode());
		String message = "COMPLETED".equals(response.getStatus())
			? "매칭이 완료되었습니다"
			: "상대방의 인증을 기다리고 있어요";
		return ResponseEntity.ok(ApiResponse.success("S200", message, response));
	}

	@Operation(summary = "랭킹 조회", description = "완성 칸 점수 기준 상위 5명과 본인 순위를 조회합니다 (1일 1회 배치 갱신).",
		security = @SecurityRequirement(name = "Bearer Authentication"))
	@GetMapping("/ranking")
	public ResponseEntity<?> getRanking(@AuthenticationPrincipal UserDetails userDetails) {
		BingoRankingResponse response = bingoManager.getRanking(userDetails.getUsername());
		return ResponseEntity.ok(ApiResponse.success("S200", "랭킹 조회에 성공했습니다", response));
	}

	/** 매칭 규칙 위반 등 DTO 검증만으로 표현 못 하는 빙고 비즈니스 규칙 위반. */
	@ExceptionHandler(BingoException.class)
	public ResponseEntity<ApiResponse<Object>> handleBingoException(BingoException ex) {
		return ResponseEntity.status(ex.getHttpStatus())
			.body(ApiResponse.error(ex.getCode(), ex.getMessage()));
	}

	/** 빙고 처리 중 예상치 못한 오류: 명세상 500 / E500 형태로 응답한다 (엔드포인트별 메시지는 스펙 문구 그대로). */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleBingoError(Exception ex, HttpServletRequest request) {
		log.error("[빙고] 처리 실패", ex);
		String uri = request.getRequestURI();
		String message;
		if (uri.endsWith("/verify")) {
			message = "인증 처리 중 오류가 발생했습니다.";
		} else if (uri.endsWith("/ranking")) {
			message = "랭킹 조회 중 오류가 발생했습니다";
		} else {
			message = "빙고판 조회 중 오류가 발생했습니다";
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error("E500", message));
	}
}
