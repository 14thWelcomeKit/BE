package com.likelion13th.Welcomekit_BE.domain.dto.request;

import java.util.List;

import lombok.Getter;

@Getter
public class PutTeamMemberRequest {
	private List<String> executiveStudentNumList;
	private String teamName;
}
