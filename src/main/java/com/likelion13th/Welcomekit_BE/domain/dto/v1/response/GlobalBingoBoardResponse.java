package com.likelion13th.Welcomekit_BE.domain.dto.v1.response;

import com.likelion13th.Welcomekit_BE.domain.enums.v1.BingoCellStatusV1;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalBingoBoardResponse {

	private List<CellSummary> cells;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CellSummary {
		private Long cellId;
		private BingoCellStatusV1 status;
		/** null 이면 EMPTY */
		private String imageUrl;
		/** null 이면 EMPTY */
		private String teamName;
	}
}
