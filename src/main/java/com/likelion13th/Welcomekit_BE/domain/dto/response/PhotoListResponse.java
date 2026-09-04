package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 사진첩 목록 조회 응답 페이로드( {@code data} ).
 */
@Getter
@Builder
@Schema(description = "사진첩 목록 조회 응답")
public class PhotoListResponse {

	@Schema(description = "게시글 요약 목록 (게시일 내림차순)")
	private final List<PhotoSummary> posts;

	@Schema(description = "페이지네이션 정보")
	private final PageInfo pageInfo;

	@Getter
	@Builder
	@Schema(description = "사진첩 게시글 요약")
	public static class PhotoSummary {

		@Schema(description = "게시글 ID", example = "5")
		private final Long postId;

		@Schema(description = "제목", example = "14기 MT 스케치")
		private final String title;

		@Schema(description = "기수 카테고리", example = "14기")
		private final String category;

		@Schema(description = "썸네일(첫 번째 사진) URL. 사진이 없으면 null", example = "https://.../thumb.jpg")
		private final String thumbnailUrl;

		@Schema(description = "게시일 (YYYY-MM-DD)", example = "2026-08-20")
		private final String postedAt;
	}

	@Getter
	@Builder
	@Schema(description = "페이지네이션 정보")
	public static class PageInfo {

		@Schema(description = "현재 페이지 번호", example = "1")
		private final int page;

		@Schema(description = "페이지당 개수", example = "12")
		private final int size;

		@Schema(description = "전체 게시글 수", example = "34")
		private final long totalElements;

		@Schema(description = "전체 페이지 수", example = "3")
		private final int totalPages;
	}
}
