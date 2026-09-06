package com.likelion13th.Welcomekit_BE.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BingoMyRankingResponse {
	private Integer rank;
	private Long userId;
	private String nickname;
	private Integer score;
}
