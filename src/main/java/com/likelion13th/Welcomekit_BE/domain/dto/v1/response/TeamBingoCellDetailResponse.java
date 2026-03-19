package com.likelion13th.Welcomekit_BE.domain.dto.v1.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion13th.Welcomekit_BE.domain.enums.v1.BingoCellStatusV1;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamBingoCellDetailResponse {

	private Long cellId;
	private BingoCellStatusV1 status;
	private MissionInfo mission;
	private DisplayData displayData;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime updatedAt;

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
		private String teamName;
		/** null 이면 EMPTY */
		private String imageUrl;
		/** PROCESSING 일 때만 존재(초 단위). 그 외 null */
		private Long remainingSeconds;
		/** OCCUPIED 이면 true */
		private Boolean isConfirmed;
	}
}
