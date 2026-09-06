package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "문의글 상세 조회 응답")
public class QnaDetailResponse {

	@Schema(description = "문의글 ID", example = "5")
	private final Long qnaId;

	@Schema(description = "제목", example = "출석 QR이 안 열려요")
	private final String title;

	@Schema(description = "내용")
	private final String content;

	@Schema(description = "작성자 사용자 ID", example = "1")
	private final Long authorId;

	@Schema(description = "작성자 이름", example = "김주희")
	private final String authorName;

	@Schema(description = "작성일시")
	private final LocalDateTime createdAt;

	@Schema(description = "요청자 본인이 작성한 글인지 여부 (삭제 버튼 노출용)")
	private final boolean isOwner;
}
