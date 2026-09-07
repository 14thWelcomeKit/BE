package com.likelion13th.Welcomekit_BE.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 프로필 이미지 업로드용 presigned URL 발급 응답. */
@Getter
@AllArgsConstructor
@Schema(description = "프로필 이미지 업로드 URL 발급 응답")
public class ProfileImageUploadUrlResponse {

	@Schema(description = "S3 presigned PUT URL. 이 URL로 이미지 바이너리를 직접 PUT (유효시간 5분)")
	private String uploadUrl;

	@Schema(description = "업로드 완료 후 실제로 쓰일 공개 URL. PATCH /user/profileImage 의 fileUrl 에 사용")
	private String fileUrl;
}
