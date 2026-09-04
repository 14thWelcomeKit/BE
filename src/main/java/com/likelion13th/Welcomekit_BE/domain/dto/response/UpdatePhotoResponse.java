package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 사진첩 게시글 수정 응답.
 */
@Getter
@Builder
@Schema(description = "사진첩 게시글 수정 응답")
public class UpdatePhotoResponse {

	@Schema(description = "게시글 ID", example = "5")
	private final Long postId;

	@Schema(description = "수정된 제목", example = "14기 MT 스케치 (수정)")
	private final String title;

	@Schema(description = "기수 카테고리", example = "13기")
	private final String category;

	@Schema(description = "행사일 (YYYY-MM-DD)", example = "2026-08-25")
	private final String eventDate;

	@Schema(description = "현재 썸네일(첫 번째 사진) URL")
	private final String thumbnailUrl;

	@Schema(description = "수정 후 전체 사진 URL 배열 (업로드 순서)")
	private final List<String> photoUrls;
}
