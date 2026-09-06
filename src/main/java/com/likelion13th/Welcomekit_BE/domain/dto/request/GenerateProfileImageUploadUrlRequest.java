package com.likelion13th.Welcomekit_BE.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

/** 프로필 이미지 업로드용 presigned URL 발급 요청. */
@Getter
public class GenerateProfileImageUploadUrlRequest {

	@NotBlank(message = "E400_FILE_TYPE:지원하지 않는 이미지 형식입니다. (jpeg, png, webp만 가능)")
	@Pattern(
		regexp = "image/(jpeg|png|webp)",
		message = "E400_FILE_TYPE:지원하지 않는 이미지 형식입니다. (jpeg, png, webp만 가능)"
	)
	@Schema(description = "MIME 타입 (jpeg/png/webp 만 지원)", example = "image/jpeg")
	private String contentType;
}
