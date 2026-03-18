package com.likelion13th.Welcomekit_BE.domain.enums.v1;

public enum BingoCellStatusV1 {
	EMPTY,      // 아직 아무 팀도 도전하지 않은 상태
	PROCESSING, // 한 팀이 사진을 올리고 12시간 타이머 진행 중
	OCCUPIED    // 12시간 타이머 만료, 해당 팀의 점유 확정
}
