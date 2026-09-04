package com.likelion13th.Welcomekit_BE.domain.dto.request;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 사진첩 게시글 수정 요청. PATCH 이므로 모든 필드가 선택이며,
 * null 인 필드는 값을 바꾸지 않는다 (title/category/eventDate/content 공통).
 */
@Getter
public class UpdateWelcomeKitPhotoRequest {

	@Size(max = 30, message = "E400_TITLE:제목은 30자 이내로 입력해주세요.")
	@Schema(description = "제목 (선택, 최대 30자)")
	private String title;

	@Schema(description = "기수 카테고리 (선택)")
	private String category;

	@Schema(description = "행사일 (선택, YYYY-MM-DD)")
	private LocalDate eventDate;

	@Size(max = 1000, message = "E401_CONTENT:내용은 1000자 이내로 입력해주세요.")
	@Schema(description = "내용 (선택, 최대 1000자)")
	private String content;

	@Schema(description = "추가할 사진 URL 목록 (선택)")
	private List<String> addPhotoUrls;

	@Schema(description = "삭제할 사진 ID 목록 (선택). 상세 조회 응답의 photoIds 값 사용")
	private List<Long> deletePhotoIds;
}
