package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BingoRankingResponse {
	private String updatedAt;
	private List<BingoRankerResponse> topRankers;
	private BingoMyRankingResponse myRanking;
}
