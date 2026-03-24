package com.likelion13th.Welcomekit_BE.service.v1;

import com.likelion13th.Welcomekit_BE.domain.enums.v1.BingoCellStatusV1;
import com.likelion13th.Welcomekit_BE.domain.enums.v1.BingoMissionV1;
import com.likelion13th.Welcomekit_BE.domain.v1.BingoCellV1;
import com.likelion13th.Welcomekit_BE.repository.v1.BingoCellV1Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 기동 시 14기 빙고 글로벌 보드(25칸)를 초기화한다.
 * 이미 25개 셀이 존재하면 아무 작업도 하지 않는다 (멱등성 보장).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BingoBoardInitializer implements ApplicationRunner {

	private final BingoCellV1Repository bingoCellV1Repository;

	@Override
	public void run(ApplicationArguments args) {
		if (bingoCellV1Repository.count() >= 25) {
			log.info("[BingoV1] 글로벌 빙고판이 이미 초기화되어 있습니다. 초기화를 건너뜁니다.");
			return;
		}

		BingoMissionV1[] missions = BingoMissionV1.values(); // 반드시 25개
		for (int i = 0; i < 25; i++) {
			BingoCellV1 cell = BingoCellV1.builder()
				.id((long) (i + 1))
				.mission(missions[i].getTitle())
				.status(BingoCellStatusV1.EMPTY)
				.build();
			bingoCellV1Repository.save(cell);
		}
		log.info("[BingoV1] 글로벌 빙고판 25칸 초기화 완료.");
	}
}
