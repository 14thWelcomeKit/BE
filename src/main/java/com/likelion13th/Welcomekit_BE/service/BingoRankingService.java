package com.likelion13th.Welcomekit_BE.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13th.Welcomekit_BE.domain.Bingo;
import com.likelion13th.Welcomekit_BE.domain.BingoCell;
import com.likelion13th.Welcomekit_BE.domain.BingoRanking;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoMyRankingResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoRankerResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoRankingResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.BingoCellStatus;
import com.likelion13th.Welcomekit_BE.repository.BingoRankingRepository;
import com.likelion13th.Welcomekit_BE.repository.BingoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 빙고 랭킹은 실시간이 아닌 1일 1회 배치 갱신 데이터다.
 * {@link #refreshRankings()} 가 전체 스냅샷({@link BingoRanking})을 다시 계산해 덮어쓰고,
 * 조회 API는 항상 이 스냅샷만 읽는다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BingoRankingService {
	private static final int TOP_RANK = 5;
	private static final ZoneId KST = ZoneId.of("Asia/Seoul");
	private static final DateTimeFormatter UPDATED_AT_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	@Autowired
	private final BingoRepository bingoRepository;
	@Autowired
	private final BingoRankingRepository bingoRankingRepository;

	@Transactional(readOnly = true)
	public BingoRankingResponse getRanking(User user) {
		List<BingoRanking> topRankings = bingoRankingRepository.findByRankLessThanEqualOrderByRankAsc(TOP_RANK);

		String updatedAt = topRankings.stream()
			.findFirst()
			.map(ranking -> ranking.getUpdatedAt().atZone(KST).format(UPDATED_AT_FORMATTER))
			.orElse(null);

		List<BingoRankerResponse> topRankers = topRankings.stream()
			.map(ranking -> new BingoRankerResponse(ranking.getRank(), ranking.getUser().getId(),
				ranking.getUser().getUserName(), ranking.getUser().getProfileImage(), ranking.getScore()))
			.toList();

		BingoMyRankingResponse myRanking = bingoRankingRepository.findByUser(user)
			.map(ranking -> new BingoMyRankingResponse(ranking.getRank(), user.getId(), user.getUserName(),
				ranking.getScore()))
			.orElseGet(() -> new BingoMyRankingResponse(null, user.getId(), user.getUserName(), 0));

		return new BingoRankingResponse(updatedAt, topRankers, myRanking);
	}

	/** 매일 자정(KST) 랭킹 스냅샷을 다시 계산한다. */
	@Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
	@Transactional
	public void refreshRankings() {
		LocalDateTime batchTime = LocalDateTime.now(KST);

		List<Bingo> bingos = bingoRepository.findAll();
		List<ScoredUser> scoredUsers = bingos.stream()
			.map(bingo -> new ScoredUser(bingo.getUser(), completedScoreOf(bingo)))
			.sorted(Comparator.comparingInt(ScoredUser::score).reversed())
			.toList();

		List<BingoRanking> rankings = new ArrayList<>();
		int rank = 0;
		int previousScore = Integer.MIN_VALUE;
		for (int i = 0; i < scoredUsers.size(); i++) {
			ScoredUser scoredUser = scoredUsers.get(i);
			if (scoredUser.score() != previousScore) {
				rank = i + 1;
				previousScore = scoredUser.score();
			}

			BingoRanking ranking = bingoRankingRepository.findByUser(scoredUser.user())
				.orElseGet(() -> BingoRanking.builder().user(scoredUser.user()).build());
			ranking.setScore(scoredUser.score());
			ranking.setRank(rank);
			ranking.setUpdatedAt(batchTime);
			rankings.add(ranking);
		}

		bingoRankingRepository.saveAll(rankings);
		log.info("[빙고 랭킹] {}명 스냅샷 갱신 완료 ({})", rankings.size(), batchTime);
	}

	/**
	 * 로컬/개발 환경에서 자정까지 기다리지 않고도 확인할 수 있도록, 앱 기동 시 한 번 계산해둔다.
	 * refreshRankings() 는 같은 빈 내부에서 호출하면 프록시를 거치지 않아 @Transactional 이
	 * 적용되지 않으므로(self-invocation), 이 메서드에도 별도로 @Transactional 을 붙인다.
	 */
	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public void refreshRankingsOnStartup() {
		refreshRankings();
	}

	private int completedScoreOf(Bingo bingo) {
		return bingo.getCells().stream()
			.filter(cell -> cell.getStatus() == BingoCellStatus.COMPLETED)
			.mapToInt(cell -> cell.getMission().getPoints())
			.sum();
	}

	private record ScoredUser(User user, int score) {
	}
}
