package com.likelion13th.Welcomekit_BE.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BingoRankerResponse {
	private Integer rank;
	private Long userId;
	private String nickname;
	private String profileImageUrl;
	private Integer score;
}
