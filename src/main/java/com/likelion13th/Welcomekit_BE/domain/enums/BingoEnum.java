package com.likelion13th.Welcomekit_BE.domain.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

@Getter
public enum BingoEnum {
	MISSION_1("첫 번째 미션"),
	MISSION_2("두 번째 미션"),
	MISSION_3("세 번째 미션"),
	MISSION_4("네 번째 미션"),
	MISSION_5("다섯 번째 미션"),
	MISSION_6("여섯 번째 미션"),
	MISSION_7("일곱 번째 미션"),
	MISSION_8("여덟 번째 미션"),
	MISSION_9("아홉 번째 미션"),
	MISSION_10("열 번째 미션"),
	MISSION_11("열 번째 미션"),
	MISSION_12("열 번째 미션"),
	MISSION_13("열 번째 미션"),
	MISSION_14("열 번째 미션"),
	MISSION_15("열 번째 미션"),
	MISSION_16("열 번째 미션"),
	MISSION_17("열 번째 미션"),
	MISSION_18("열 번째 미션"),
	MISSION_19("열 번째 미션"),
	MISSION_20("열 번째 미션"),
	MISSION_21("열 번째 미션"),
	MISSION_22("열 번째 미션"),
	MISSION_23("열 번째 미션"),
	MISSION_24("열 번째 미션"),
	MISSION_25("열 번째 미션");

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
