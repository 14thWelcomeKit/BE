package com.likelion13th.Welcomekit_BE.domain.dto.response;

import com.likelion13th.Welcomekit_BE.domain.enums.DevPart;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "내 정보 조회 응답")
public class GetMyInfoResponse {
	private String name;
	private String studentName;
	private String teamName;
	private DevPart devPart;
	private String profileImage;
	private Boolean hasReadWelcome;

	@Schema(description = "사용자 권한. ADMIN(운영진) / BABY_LION(일반). 프론트에서 운영진 전용 메뉴 노출 제어에 사용",
		example = "BABY_LION")
	private UserType userType;
}
