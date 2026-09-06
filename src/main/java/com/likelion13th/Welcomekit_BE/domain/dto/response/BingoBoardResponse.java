package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BingoBoardResponse {
	private String myCode;
	private List<BingoCellResponse> cells;
}
