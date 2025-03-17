package com.likelion13th.Welcomekit_BE.domain.dto.response;

import com.likelion13th.Welcomekit_BE.domain.enums.BingoEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMyBingoResponse {
	private Long id;
	private String mission;
	private Boolean isComplete;
	private Boolean isRevealed;
}
