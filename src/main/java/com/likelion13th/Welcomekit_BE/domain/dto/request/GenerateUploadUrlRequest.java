package com.likelion13th.Welcomekit_BE.domain.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 사진첩 이미지 업로드용 presigned URL 발급 요청.
 * 파일 바이트 자체는 안 받고, 각 파일의 contentType 만으로 요청한다.
 */
@Getter
public class GenerateUploadUrlRequest {

	@NotEmpty(message = "E400_FILE_COUNT:사진은 최대 5장까지 업로드할 수 있습니다.")
	@Size(max = 5, message = "E400_FILE_COUNT:사진은 최대 5장까지 업로드할 수 있습니다.")
	@Valid
	@Schema(description = "업로드할 파일 정보 목록 (1~5개)")
	private List<FileInfo> files;

	@Getter
	public static class FileInfo {

		@NotBlank(message = "E401_FILE_TYPE:지원하지 않는 이미지 형식입니다. (jpeg, png, webp만 가능)")
		@Pattern(
			regexp = "image/(jpeg|png|webp)",
			message = "E401_FILE_TYPE:지원하지 않는 이미지 형식입니다. (jpeg, png, webp만 가능)"
		)
		@Schema(description = "MIME 타입 (jpeg/png/webp 만 지원, HEIC 등은 프론트에서 변환 후 요청)", example = "image/jpeg")
		private String contentType;
	}
}
