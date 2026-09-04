package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.ApiResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.PhotoListResponse;
import com.likelion13th.Welcomekit_BE.service.WelcomeKitPhotoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사진첩 API. 공통 Base URL({@code /api/v3/welcome-kit}) 이 자동으로 붙어
 * 실제 매핑은 {@code /api/v3/welcome-kit/photos} 이다.
 */
@Slf4j
@RestController
@RequestMapping("/photos")
@RequiredArgsConstructor
@Tag(name = "사진첩", description = "기수별 사진첩 게시글 조회/작성/수정/삭제 API")
public class WelcomeKitPhotoController {

	private final WelcomeKitPhotoService photoService;

	@Operation(
		summary = "사진첩 목록 조회",
		description = "기수별 사진첩 게시글을 게시일 기준 내림차순으로 페이지네이션 조회합니다. "
			+ "category 로 특정 기수만 필터링할 수 있으며, 미입력 시 전체 기수를 조회합니다. "
			+ "각 게시글은 제목, 기수 카테고리, 썸네일(첫 번째 사진), 게시일(YYYY-MM-DD)을 포함합니다."
	)
	@GetMapping
	public ApiResponse<PhotoListResponse> getPhotos(
		@Parameter(description = "페이지 번호 (0-based, 선택, 기본값 0)", example = "0")
		@RequestParam(defaultValue = "0") int page,
		@Parameter(description = "페이지당 개수 (선택, 기본값 12)", example = "12")
		@RequestParam(defaultValue = "12") int size,
		@Parameter(description = "기수 카테고리 필터 (선택, 미입력 시 전체)", example = "14기")
		@RequestParam(required = false) String category
	) {
		PhotoListResponse data = photoService.getPhotoList(page, size, category);
		return ApiResponse.success("S200", "사진첩 목록 조회에 성공했습니다", data);
	}

	/** 사진첩 조회 중 예상치 못한 오류: 명세상 500 / E500 형태로 응답한다. */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handlePhotoError(Exception ex) {
		log.error("[사진첩] 목록 조회 실패", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error("E500", "사진첩 목록 조회 중 오류가 발생했습니다"));
	}
}
