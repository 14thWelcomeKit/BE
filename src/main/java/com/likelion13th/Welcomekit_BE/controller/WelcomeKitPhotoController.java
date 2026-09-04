package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.ApiResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateWelcomeKitPhotoRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.GenerateUploadUrlRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.UpdateWelcomeKitPhotoRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.PhotoDetailResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.PhotoListResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.UpdatePhotoResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.UploadUrlResponse;
import com.likelion13th.Welcomekit_BE.exception.PhotoException;
import com.likelion13th.Welcomekit_BE.service.WelcomeKitPhotoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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

	@Operation(
		summary = "사진첩 게시글 상세 조회",
		description = "게시글 하나의 제목/전체 사진 목록/내용/행사일/작성자 정보를 조회합니다. 비로그인도 조회 가능하며, "
			+ "로그인 상태에서 본인이 작성한 글이면 isOwner 가 true 로 내려갑니다 (수정/삭제 버튼 노출용)."
	)
	@GetMapping("/{postId}")
	public ApiResponse<PhotoDetailResponse> getPhotoDetail(
		@Parameter(description = "조회할 게시글 ID", example = "5")
		@PathVariable Long postId,
		@AuthenticationPrincipal(errorOnInvalidType = false) UserDetails userDetails
	) {
		String requesterEmail = userDetails != null ? userDetails.getUsername() : null;
		PhotoDetailResponse data = photoService.getPhotoDetail(postId, requesterEmail);
		return ApiResponse.success("S200", "게시글 조회에 성공했습니다", data);
	}

	@Operation(
		summary = "사진첩 게시글 작성",
		description = "운영진만 작성 가능합니다. 사진 1~5장, 제목 최대 30자, 내용 최대 1000자, "
			+ "첫 번째 사진이 썸네일로 지정됩니다.",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@PostMapping
	public ResponseEntity<ApiResponse<PhotoListResponse.PhotoSummary>> createPost(
		@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody CreateWelcomeKitPhotoRequest request
	) {
		PhotoListResponse.PhotoSummary data = photoService.createPost(userDetails.getUsername(), request);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("S201", "게시글이 등록되었습니다", data));
	}

	@Operation(
		summary = "사진첩 이미지 업로드 URL 발급",
		description = "운영진만 요청 가능합니다. 한 번에 최대 5개까지 S3 presigned PUT URL을 발급합니다 (유효시간 5분). "
			+ "발급받은 uploadUrl로 프론트가 S3에 직접 PUT하고, fileUrl을 게시글 작성 API(POST /photos)의 photoUrls 에 사용합니다. "
			+ "허용 형식은 jpeg/png/webp 이며, HEIC 등은 프론트에서 변환 후 요청해야 합니다.",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@PostMapping("/upload-url")
	public ApiResponse<UploadUrlResponse> generateUploadUrls(
		@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody GenerateUploadUrlRequest request
	) {
		UploadUrlResponse data = photoService.generateUploadUrls(userDetails.getUsername(), request);
		return ApiResponse.success("S200", "업로드 URL 발급에 성공했습니다", data);
	}

	@Operation(
		summary = "사진첩 게시글 수정",
		description = "작성자 본인만 수정 가능합니다. title/category/eventDate/content/addPhotoUrls/deletePhotoIds "
			+ "전부 선택이며, 보낸 필드만 반영됩니다. 사진 추가/삭제 반영 후 개수는 1~5장을 유지해야 합니다. "
			+ "deletePhotoIds 는 상세 조회 응답의 photoIds 값을 사용하세요.",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@PatchMapping("/{postId}")
	public ApiResponse<UpdatePhotoResponse> updatePost(
		@Parameter(description = "수정할 게시글 ID", example = "5")
		@PathVariable Long postId,
		@AuthenticationPrincipal UserDetails userDetails,
		@Valid @RequestBody UpdateWelcomeKitPhotoRequest request
	) {
		UpdatePhotoResponse data = photoService.updatePost(postId, userDetails.getUsername(), request);
		return ApiResponse.success("S200", "게시글이 수정되었습니다", data);
	}

	@Operation(
		summary = "사진첩 게시글 삭제",
		description = "작성자 본인만 삭제 가능합니다. 게시글과 사진 레코드를 삭제하고, 업로드됐던 S3 파일도 함께 "
			+ "삭제를 시도합니다 (S3 삭제 실패는 게시글 삭제 자체를 막지 않습니다).",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@DeleteMapping("/{postId}")
	public ApiResponse<Void> deletePost(
		@Parameter(description = "삭제할 게시글 ID", example = "5")
		@PathVariable Long postId,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		photoService.deletePost(postId, userDetails.getUsername());
		return ApiResponse.success("S200", "게시글이 삭제되었습니다", null);
	}

	/** DTO 필드 검증 실패: message 에 담긴 "코드:메시지" 를 그대로 응답 code/message 로 사용한다. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
		FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
		String[] parts = fieldError.getDefaultMessage().split(":", 2);
		return ResponseEntity.badRequest().body(ApiResponse.error(parts[0], parts[1]));
	}

	/** 운영진 권한 등 DTO 검증만으로 표현 못 하는 비즈니스 규칙 위반. */
	@ExceptionHandler(PhotoException.class)
	public ResponseEntity<ApiResponse<Object>> handlePhotoException(PhotoException ex) {
		return ResponseEntity.status(ex.getHttpStatus())
			.body(ApiResponse.error(ex.getCode(), ex.getMessage()));
	}

	/** 사진첩 처리 중 예상치 못한 오류: 명세상 500 / E500 형태로 응답한다 (엔드포인트별 메시지는 스펙 문구 그대로). */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handlePhotoError(Exception ex, HttpServletRequest request) {
		log.error("[사진첩] 처리 실패", ex);
		String uri = request.getRequestURI();
		String message;
		String method = request.getMethod();
		if (uri.endsWith("/upload-url")) {
			message = "업로드 URL 발급 중 오류가 발생했습니다";
		} else if ("PATCH".equalsIgnoreCase(method)) {
			message = "게시글 수정 중 오류가 발생했습니다";
		} else if ("DELETE".equalsIgnoreCase(method)) {
			message = "게시글 삭제 중 오류가 발생했습니다";
		} else if (!"GET".equalsIgnoreCase(method)) {
			message = "게시글 등록 중 오류가 발생했습니다";
		} else if (uri.matches(".*/photos/[^/]+$")) {
			message = "게시글 조회 중 오류가 발생했습니다";
		} else {
			message = "사진첩 목록 조회 중 오류가 발생했습니다";
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(ApiResponse.error("E500", message));
	}
}
