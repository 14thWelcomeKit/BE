package com.likelion13th.Welcomekit_BE.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateQnaRequest {

	@NotBlank(message = "E400_TITLE:제목은 필수입니다.")
	@Size(max = 100, message = "E400_TITLE:제목은 100자 이내로 입력해주세요.")
	@Schema(description = "제목")
	private String title;

	@NotBlank(message = "E401_CONTENT:내용은 필수입니다.")
	@Size(max = 2000, message = "E401_CONTENT:내용은 2000자 이내로 입력해주세요.")
	@Schema(description = "내용")
	private String content;
}
