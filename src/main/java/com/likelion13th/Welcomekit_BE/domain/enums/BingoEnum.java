package com.likelion13th.Welcomekit_BE.domain.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum BingoEnum {
	MISSION_1, MISSION_2, MISSION_3, MISSION_4, MISSION_5,
	MISSION_6, MISSION_7, MISSION_8, MISSION_9, MISSION_10;

	public static List<BingoEnum> getRandomMissions(int count) {
		List<BingoEnum> missions = new ArrayList<>(Arrays.asList(values()));
		Collections.shuffle(missions);
		return missions.subList(0, count);
	}
}
