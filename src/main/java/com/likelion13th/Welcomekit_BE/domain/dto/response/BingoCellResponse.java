package com.likelion13th.Welcomekit_BE.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BingoCellResponse {
	private Integer cellId;
	private String missionContent;
	private String status;
	private String matchedWithName;
}
