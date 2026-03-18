package com.likelion13th.Welcomekit_BE.domain.dto.v1.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamInfoV1Response {

	private Long teamId;
	private String teamName;
	private List<String> members;
	private Progress progress;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Progress {
		/** 점유 확정(OCCUPIED) 셀 수 */
		private long occupiedCount;
		private int totalCells;
		/** OCCUPIED 셀 수 기준 내림차순 순위 (공동 순위 지원) */
		private int ranking;
	}
}
