package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 사진첩 이미지 업로드용 presigned URL 발급 응답.
 */
@Getter
@Builder
@Schema(description = "업로드 URL 발급 응답")
public class UploadUrlResponse {

	@Schema(description = "요청한 파일 개수만큼의 업로드/공개 URL 쌍")
	private final List<UrlPair> urls;

	@Getter
	@Builder
	@Schema(description = "presigned 업로드 URL과 업로드 완료 후 공개 URL")
	public static class UrlPair {

		@Schema(description = "S3 presigned PUT URL. 이 URL로 파일 바이너리를 직접 PUT (유효시간 5분)",
			example = "https://welcomekit14-photos.s3.ap-northeast-2.amazonaws.com/photos/{uuid}.jpg?X-Amz-...")
		private final String uploadUrl;

		@Schema(description = "업로드 완료 후 실제로 쓰일 공개 URL. POST /photos 의 photoUrls 에 사용",
			example = "https://welcomekit14-photos.s3.ap-northeast-2.amazonaws.com/photos/{uuid}.jpg")
		private final String fileUrl;
	}
}
