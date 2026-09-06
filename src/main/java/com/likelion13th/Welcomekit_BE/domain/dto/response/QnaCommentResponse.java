package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "문의 댓글")
public class QnaCommentResponse {

	@Schema(description = "댓글 ID", example = "11")
	private final Long commentId;

	@Schema(description = "댓글 내용")
	private final String content;

	@Schema(description = "작성자 사용자 ID", example = "2")
	private final Long authorId;

	@Schema(description = "작성자 이름", example = "오현우")
	private final String authorName;

	@Schema(description = "운영진 답변 여부")
	private final boolean isAdminComment;

	@Schema(description = "요청자 본인이 작성한 댓글인지 여부 (삭제 버튼 노출용)")
	private final boolean isOwner;

	@Schema(description = "작성일시")
	private final LocalDateTime createdAt;
}
