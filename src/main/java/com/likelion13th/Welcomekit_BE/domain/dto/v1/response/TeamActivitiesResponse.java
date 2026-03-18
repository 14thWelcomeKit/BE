package com.likelion13th.Welcomekit_BE.domain.dto.v1.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion13th.Welcomekit_BE.domain.enums.v1.ActivityLogStatusV1;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamActivitiesResponse {

	private List<ActivityEntry> activities;

	@Getter
	@Setter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ActivityEntry {
		private Long cellId;
		private ActivityLogStatusV1 status;
		private String message;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private LocalDateTime timestamp;
	}
}
