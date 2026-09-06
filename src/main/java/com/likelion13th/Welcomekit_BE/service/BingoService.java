package com.likelion13th.Welcomekit_BE.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13th.Welcomekit_BE.domain.Bingo;
import com.likelion13th.Welcomekit_BE.domain.BingoCell;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoBoardResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoCellResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.BingoEnum;
import com.likelion13th.Welcomekit_BE.repository.BingoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BingoService {
	private static final int BOARD_SIZE = 25;

	@Autowired
	private final BingoRepository bingoRepository;

	private final SecureRandom random = new SecureRandom();

	@Transactional
	public BingoBoardResponse getBingoBoard(User user) {
		Bingo bingo = bingoRepository.findByUser(user)
			.orElseGet(() -> createBingoBoard(user));

		List<BingoCellResponse> cells = bingo.getCells().stream()
			.sorted((a, b) -> Integer.compare(a.getPosition(), b.getPosition()))
			.map(this::toCellResponse)
			.toList();

		return new BingoBoardResponse(bingo.getCode(), cells);
	}

	private BingoCellResponse toCellResponse(BingoCell cell) {
		String status = cell.getIsComplete() ? "COMPLETED" : "INCOMPLETE";
		String matchedWithName = cell.getIsComplete() && cell.getMatchedUser() != null
			? cell.getMatchedUser().getUserName()
			: null;
		return new BingoCellResponse(cell.getPosition(), cell.getMission().getDescription(), status, matchedWithName);
	}

	private Bingo createBingoBoard(User user) {
		Bingo bingo = Bingo.builder()
			.user(user)
			.code(generateUniqueCode())
			.cells(new ArrayList<>())
			.build();

		List<BingoEnum> missions = BingoEnum.getRandomMissions(BOARD_SIZE);
		List<BingoCell> cells = new ArrayList<>();
		for (int i = 0; i < missions.size(); i++) {
			cells.add(BingoCell.builder()
				.bingo(bingo)
				.position(i + 1)
				.mission(missions.get(i))
				.isComplete(false)
				.build());
		}
		bingo.setCells(cells);

		return bingoRepository.save(bingo);
	}

	private String generateUniqueCode() {
		String code;
		do {
			code = String.format("%04d", random.nextInt(10000));
		} while (bingoRepository.existsByCode(code));
		return code;
	}
}
