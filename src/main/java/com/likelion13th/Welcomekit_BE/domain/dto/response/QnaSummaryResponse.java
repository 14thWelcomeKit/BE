package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "문의글 요약")
public class QnaSummaryResponse {

	@Schema(description = "문의글 ID", example = "5")
	private final Long qnaId;

	@Schema(description = "제목", example = "출석 QR이 안 열려요")
	private final String title;

	@Schema(description = "작성자 이름", example = "김주희")
	private final String authorName;

	@Schema(description = "작성일시")
	private final LocalDateTime createdAt;
}
