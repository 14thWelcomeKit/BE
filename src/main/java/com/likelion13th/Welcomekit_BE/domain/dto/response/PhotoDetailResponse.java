package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * 사진첩 게시글 상세 조회 응답.
 */
@Getter
@Builder
@Schema(description = "사진첩 게시글 상세 조회 응답")
public class PhotoDetailResponse {

	@Schema(description = "게시글 ID", example = "5")
	private final Long postId;

	@Schema(description = "제목", example = "14기 MT 스케치")
	private final String title;

	@Schema(description = "기수 카테고리", example = "14기")
	private final String category;

	@Schema(description = "사진 URL 전체 목록 (업로드 순서)")
	private final List<String> photoUrls;

	@Schema(description = "사진 ID 전체 목록. photoUrls 와 순서가 1:1 대응하며, "
		+ "게시글 수정 API(PATCH /photos/{postId})의 deletePhotoIds 에 사용")
	private final List<Long> photoIds;

	@Schema(description = "내용", example = "1박 2일 즐거웠던 순간들")
	private final String content;

	@Schema(description = "행사일 (YYYY-MM-DD)", example = "2026-08-20")
	private final String eventDate;

	@Schema(description = "작성자 닉네임", example = "한림")
	private final String authorNickname;

	// Lombok 의 @Getter 는 boolean isOwner 필드에 isOwner() 게터를 만드는데,
	// Jackson 이 이걸 "owner" 프로퍼티로도 같이 잡아서 isOwner/owner 가 중복 노출된다.
	// 그래서 이 필드만 Lombok 게터를 끄고 직접 게터를 써서 JSON 필드명을 isOwner 하나로 고정한다.
	@Getter(AccessLevel.NONE)
	@Schema(description = "요청자가 작성자 본인인지 여부. 비로그인 요청이면 항상 false", example = "true")
	private final boolean isOwner;

	@JsonProperty("isOwner")
	public boolean isOwner() {
		return isOwner;
	}
}
