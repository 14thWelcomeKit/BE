package com.likelion13th.Welcomekit_BE.domain.dto.v1.response;

import com.likelion13th.Welcomekit_BE.domain.enums.v1.BingoCellStatusV1;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamBingoBoardResponse {

	private String teamName;
	private List<TeamCellSummary> cells;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class TeamCellSummary {
		private Long cellId;
		private String mission;
		private BingoCellStatusV1 status;
		private Boolean isOurTeam;
		/**
		 * 테두리 색상 힌트 (프론트 렌더링용):
		 * "NONE"             - EMPTY (도전 가능)
		 * "PROCESSING_OURS"  - 우리 팀이 12시간 타이머 진행 중
		 * "PROCESSING_OTHERS"- 다른 팀이 12시간 타이머 진행 중
		 * "BLACK"            - 우리 팀이 점유 확정
		 * "GRAY"             - 다른 팀이 점유 확정
		 */
		private String borderType;
		/** null 이면 EMPTY */
		private String imageUrl;
	}
}
