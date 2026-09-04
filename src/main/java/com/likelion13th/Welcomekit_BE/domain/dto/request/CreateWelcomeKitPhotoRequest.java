package com.likelion13th.Welcomekit_BE.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class CreateWelcomeKitPhotoRequest {

    @NotBlank(message = "E400_TITLE:제목은 30자 이내로 입력해주세요.")
    @Size(max = 30, message = "E400_TITLE:제목은 30자 이내로 입력해주세요.")
    @Schema(description = "제목")
    private String title;

    @NotBlank(message = "E403_CATEGORY:기수 카테고리를 선택해주세요.")
    @Schema(description = "기수 카테고리")
    private String category;

    @NotEmpty(message = "E402_PHOTO:사진은 최대 5장까지 업로드할 수 있습니다.")
    @Size(max = 5, message = "E402_PHOTO:사진은 최대 5장까지 업로드할 수 있습니다.")
    @Schema(description = "사진")
    private List<String> photoUrls;

    @Size(max = 1000, message = "E401_CONTENT:내용은 1000자 이내로 입력해주세요.")
    @Schema(description = "사진 글 내용")
    private String content;

    @NotNull(message = "E404_EVENT_DATE:행사일을 입력해주세요.")
    @Schema(description = "사진에 관련된 행사의 날짜")
    private LocalDate eventDate;

}