package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "문의글 목록 조회 응답")
public class QnaListResponse {

	@Schema(description = "문의글 요약 목록 (최신순). 운영진은 전체, 일반 부원은 본인 글만 내려간다")
	private final List<QnaSummaryResponse> qnas;

	@Schema(description = "페이지네이션 정보")
	private final PageInfo pageInfo;

	@Getter
	@Builder
	@Schema(description = "페이지네이션 정보")
	public static class PageInfo {

		@Schema(description = "현재 페이지 번호", example = "0")
		private final int page;

		@Schema(description = "페이지당 개수", example = "10")
		private final int size;

		@Schema(description = "전체 문의글 수", example = "23")
		private final long totalElements;

		@Schema(description = "전체 페이지 수", example = "3")
		private final int totalPages;
	}
}
