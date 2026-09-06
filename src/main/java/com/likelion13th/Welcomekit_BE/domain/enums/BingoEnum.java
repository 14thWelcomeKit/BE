package com.likelion13th.Welcomekit_BE.domain.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public enum BingoEnum {
	// 1단계 - 가벼운 TMI (1점)
	MISSION_1("나와 MBTI 맨 앞자리(E/I)가 반대인 사람", 1),
	MISSION_2("오늘 나와 비슷한 색깔의 상의를 입은 사람", 1),
	MISSION_3("탕수육 부먹/찍먹 취향이 나와 딱 맞는 사람", 1),
	MISSION_4("민트초코를 내 돈 주고 사 먹는 사람 (또는 절대 안 먹는 사람)", 1),
	MISSION_5("학교까지 통학 시간 왕복 2시간 이상인 '프로통학러'", 1),
	MISSION_6("나와 출신 지역(또는 거주 동네)이 같은 사람", 1),
	MISSION_7("이름에 나와 같은 글자가 하나라도 들어가는 사람", 1),
	MISSION_8("나와 생일이 같은 달인 사람", 1),
	MISSION_9("나와 같은 과목을 수강하는 사람", 1),

	// 2단계 - 개발자 & 멋사 공감대 (2점)
	MISSION_10("나와 다른 파트인 사람 (프론트/백)", 2),
	MISSION_11("나와 다른 OS를 사용하는 사람 (맥북/윈도우)", 2),
	MISSION_12("나와 같은 주력 언어를 사용하는 사람 (Java/Python/JavaScript 등)", 2),
	MISSION_13("나와 같은 IDE를 사용하는 사람 (VS Code/IntelliJ 등)", 2),
	MISSION_14("최근 한 달 내에 깃허브 잔디(커밋) 7일 연속 심어본 사람", 2),
	MISSION_15("GitHub 프로필에 직접 작성한 Profile README가 있는 사람 (기본 빈 README 제외)", 2),
	MISSION_16("본인 파트와 관련된 GitHub Repository가 10개 이상인 사람", 2),
	MISSION_17("직접 배포 작업을 해본 사람", 2),

	// 3단계 - 적극적인 친목 유도 (3점)
	MISSION_18("운영진 중 한 명과 같이 셀카 찍기", 3),
	MISSION_19("서로의 최애 학교 앞 밥집 1개씩 추천해주기", 3),
	MISSION_20("다른 팀 부원과 다음 주 내로 밥약/커피챗 약속 잡기 (잡고 나서 코드 교환)", 3),
	MISSION_21("동아리방(또는 모임 장소)에서 내 양옆 자리에 앉은 사람", 3),
	MISSION_22("서로의 깃허브 맞팔(Follow) 하기", 3),
	MISSION_23("서로의 인스타그램 맞팔(Follow) 하기", 3),
	MISSION_24("운영진·아기사자 구분 없이 같은 연생인 사람과 사진 찍기", 3),
	MISSION_25("같이 카공 하기", 3);

	private final String description;
	private final int points;

	BingoEnum(String description, int points) {
		this.description = description;
		this.points = points;
	}

	public static List<BingoEnum> getRandomMissions(int count) {
		List<BingoEnum> missions = new ArrayList<>(Arrays.asList(values()));
		Collections.shuffle(missions);
		return missions.subList(0, count);
	}
}
