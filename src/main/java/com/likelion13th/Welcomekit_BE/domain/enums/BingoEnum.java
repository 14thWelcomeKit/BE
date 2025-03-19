package com.likelion13th.Welcomekit_BE.domain.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public enum BingoEnum {
	MISSION_1("인생네컷 찍어서 올리기"),
	MISSION_2("회식사진 찍어서 올리기"),
	MISSION_3("동방 모각코 / 오프라인 모각코"),
	MISSION_4("에타 시간표 공유하기"),
	MISSION_5("명수당 피크닉"),
	MISSION_6("모여서 같이 시험공부"),
	MISSION_7("같이 학식먹기 (오후 5시 이전)"),
	MISSION_8("코노/오락실/보드게임 카페 가기! "),
	MISSION_9("전원 개총/종총 참여"),
	MISSION_10("서로의 TMI 공유하기"),
	MISSION_11("팀원 생일 챙기기"),
	MISSION_12("팀 단체사진 찍기"),
	MISSION_13("같이 영화/드라마 보기"),
	MISSION_14("같이 새벽까지 코딩하기"),
	MISSION_15("서로의 개발환경 보여주기"),
	MISSION_16("팀원과 같이 과제/공부 도와주기"),
	MISSION_17("서로가 좋아하는 노래 공유하기"),
	MISSION_18("랜덤 커피 챌린지"),
	MISSION_19("학교 근처 맛집 탐방"),
	MISSION_20("팀원과 함께 사진 찍어서 프사로 설정하기"),
	MISSION_21("랜덤 팀원 인터뷰하기"),
	MISSION_22("코딩 챌린지 or 문제 풀기 대결"),
	MISSION_23("팀원과 같은 색깔 옷 입고 사진 찍기"),
	MISSION_24("멋사 포즈 or 13 그려서 사진 찍기"),
	MISSION_25("외대 뒷산 올라서 다같이 기념 사진 찍기");

	private final String description;

	BingoEnum(String description) {
		this.description = description;
	}

	public static List<BingoEnum> getRandomMissions(int count) {
		List<BingoEnum> missions = new ArrayList<>(Arrays.asList(values()));
		Collections.shuffle(missions);
		return missions.subList(0, count);
	}
}
