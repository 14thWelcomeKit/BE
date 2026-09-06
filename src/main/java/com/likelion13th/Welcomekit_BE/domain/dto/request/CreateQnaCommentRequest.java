package com.likelion13th.Welcomekit_BE.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateQnaCommentRequest {

	@NotNull(message = "E400_QNA_ID:qnaId는 필수입니다.")
	@Schema(description = "댓글을 작성할 문의글 ID")
	private Long qnaId;

	@NotBlank(message = "E401_CONTENT:댓글 내용은 필수입니다.")
	@Size(max = 1000, message = "E401_CONTENT:댓글은 1000자 이내로 입력해주세요.")
	@Schema(description = "댓글 내용")
	private String content;
}
