package com.likelion13th.Welcomekit_BE.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/** S3 업로드 완료 후, 실제 프로필 이미지로 반영할 URL. */
@Getter
public class UpdateProfileImageRequest {

	@NotBlank(message = "E400_FILE_URL:fileUrl은 필수입니다.")
	@Schema(description = "업로드 URL 발급 응답의 fileUrl 값", example = "https://welcomekit14-photos.s3.ap-northeast-2.amazonaws.com/profile/{uuid}.jpg")
	private String fileUrl;
}
