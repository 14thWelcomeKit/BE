package com.likelion13th.Welcomekit_BE.domain.dto.v1.response;

import com.likelion13th.Welcomekit_BE.domain.enums.v1.BingoCellStatusV1;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalBingoCellDetailResponse {

	private Long cellId;
	private BingoCellStatusV1 status;
	private MissionInfo mission;
	private DisplayData displayData;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MissionInfo {
		private String title;
		private String description;
	}

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DisplayData {
		/** null 이면 EMPTY */
		private String imageUrl;
		/** null 이면 EMPTY */
		private String teamName;
		/** "HH:mm:ss" 형식. PROCESSING 일 때만 존재, 그 외 null */
		private String remainingTime;
		/** "도전 가능" / "진행 중" / "점유 완료" */
		private String statusLabel;
	}
}
