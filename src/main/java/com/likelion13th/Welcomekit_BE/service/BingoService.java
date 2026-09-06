package com.likelion13th.Welcomekit_BE.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13th.Welcomekit_BE.domain.Bingo;
import com.likelion13th.Welcomekit_BE.domain.BingoCell;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoBoardResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoCellResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.BingoVerifyResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.BingoCellStatus;
import com.likelion13th.Welcomekit_BE.domain.enums.BingoEnum;
import com.likelion13th.Welcomekit_BE.exception.BingoException;
import com.likelion13th.Welcomekit_BE.repository.BingoCellRepository;
import com.likelion13th.Welcomekit_BE.repository.BingoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BingoService {
	private static final int BOARD_SIZE = 25;
	private static final int PENDING_HOURS = 48;
	private static final ZoneId KST = ZoneId.of("Asia/Seoul");
	private static final DateTimeFormatter EXPIRES_AT_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	@Autowired
	private final BingoRepository bingoRepository;
	@Autowired
	private final BingoCellRepository bingoCellRepository;

	private final SecureRandom random = new SecureRandom();

	@Transactional
	public BingoBoardResponse getBingoBoard(User user) {
		Bingo bingo = bingoRepository.findByUser(user)
			.orElseGet(() -> createBingoBoard(user));

		List<BingoCellResponse> cells = bingo.getCells().stream()
			.peek(this::expireIfNeeded)
			.sorted(Comparator.comparingInt(BingoCell::getPosition))
			.map(this::toCellResponse)
			.toList();

		return new BingoBoardResponse(bingo.getCode(), cells);
	}

	@Transactional
	public BingoVerifyResponse verifyCell(User user, Integer cellId, String opponentCode) {
		Bingo myBingo = bingoRepository.findByUser(user)
			.orElseGet(() -> createBingoBoard(user));

		// 명세에 없는 cellId 는 별도 에러 코드 없이 500(E500)으로 처리한다.
		BingoCell myCell = bingoCellRepository.findByBingoAndPosition(myBingo, cellId).orElseThrow();
		expireIfNeeded(myCell);

		// 이미 완료된 칸 재인증도 명세에 별도 코드가 없어 E401_DUP 로 처리한다.
		if (myCell.getStatus() == BingoCellStatus.COMPLETED) {
			throw new BingoException(HttpStatus.BAD_REQUEST, "E401_DUP", "이미 해당 사용자와 매칭을 완료했습니다.");
		}

		if (myBingo.getCode().equals(opponentCode)) {
			throw new BingoException(HttpStatus.BAD_REQUEST, "E403_SELF", "본인의 코드는 입력할 수 없습니다.");
		}

		Bingo opponentBingo = bingoRepository.findByCode(opponentCode)
			.orElseThrow(() -> new BingoException(HttpStatus.BAD_REQUEST, "E400",
				"존재하지 않는 코드입니다. 코드를 다시 입력해주세요."));
		User opponentUser = opponentBingo.getUser();

		boolean alreadyMatchedWithOpponent = bingoCellRepository
			.existsByBingoAndMatchedUserAndStatus(myBingo, opponentUser, BingoCellStatus.COMPLETED);
		if (alreadyMatchedWithOpponent) {
			throw new BingoException(HttpStatus.BAD_REQUEST, "E401_DUP", "이미 해당 사용자와 매칭을 완료했습니다.");
		}

		List<BingoCell> opponentCellsTargetingMe = bingoCellRepository
			.findByBingoAndMatchedUser(opponentBingo, user);
		opponentCellsTargetingMe.forEach(this::expireIfNeeded);
		opponentCellsTargetingMe = opponentCellsTargetingMe.stream()
			.filter(cell -> cell.getStatus() != BingoCellStatus.INCOMPLETE)
			.toList();

		Optional<BingoCell> sameCellFromOpponent = opponentCellsTargetingMe.stream()
			.filter(cell -> cell.getPosition().equals(cellId))
			.findFirst();

		if (sameCellFromOpponent.isPresent()) {
			BingoCell opponentCell = sameCellFromOpponent.get();

			myCell.setStatus(BingoCellStatus.COMPLETED);
			myCell.setMatchedUser(opponentUser);
			myCell.setPendingExpiresAt(null);

			opponentCell.setStatus(BingoCellStatus.COMPLETED);
			opponentCell.setMatchedUser(user);
			opponentCell.setPendingExpiresAt(null);

			bingoCellRepository.save(myCell);
			bingoCellRepository.save(opponentCell);

			return new BingoVerifyResponse(cellId, BingoCellStatus.COMPLETED.name(), opponentUser.getUserName(), null);
		}

		if (!opponentCellsTargetingMe.isEmpty()) {
			throw new BingoException(HttpStatus.BAD_REQUEST, "E402_MISMATCH",
				"칸이 서로 일치하지 않아요. 같은 칸인지 다시 확인해주세요.");
		}

		LocalDateTime expiresAt = LocalDateTime.now(KST).plusHours(PENDING_HOURS);
		myCell.setStatus(BingoCellStatus.PENDING);
		myCell.setMatchedUser(opponentUser);
		myCell.setPendingExpiresAt(expiresAt);
		bingoCellRepository.save(myCell);

		String formattedExpiresAt = expiresAt.atZone(KST).format(EXPIRES_AT_FORMATTER);
		return new BingoVerifyResponse(cellId, BingoCellStatus.PENDING.name(), null, formattedExpiresAt);
	}

	private void expireIfNeeded(BingoCell cell) {
		if (cell.getStatus() == BingoCellStatus.PENDING
			&& cell.getPendingExpiresAt() != null
			&& cell.getPendingExpiresAt().isBefore(LocalDateTime.now(KST))) {
			cell.setStatus(BingoCellStatus.INCOMPLETE);
			cell.setMatchedUser(null);
			cell.setPendingExpiresAt(null);
			bingoCellRepository.save(cell);
		}
	}

	private BingoCellResponse toCellResponse(BingoCell cell) {
		String matchedWithName = cell.getStatus() == BingoCellStatus.COMPLETED && cell.getMatchedUser() != null
			? cell.getMatchedUser().getUserName()
			: null;
		return new BingoCellResponse(cell.getPosition(), cell.getMission().getDescription(),
			cell.getStatus().name(), matchedWithName);
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
				.status(BingoCellStatus.INCOMPLETE)
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
