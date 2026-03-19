package com.likelion13th.Welcomekit_BE.domain.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetWelcomeMessageResponse {

	private Long id;
	private String receiverName;
	private String senderName;
	private String content;
	private LocalDateTime createdAt;
}

