package com.likelion13th.Welcomekit_BE.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.Bingo;
import com.likelion13th.Welcomekit_BE.domain.BingoCell;
import com.likelion13th.Welcomekit_BE.domain.Team;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetMyBingoResponse;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.repository.BingoCellRepository;
import com.likelion13th.Welcomekit_BE.repository.BingoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BingoService {
	@Autowired
	private final BingoRepository bingoRepository;
	@Autowired
	private final BingoCellRepository bingoCellRepository;

	public List<GetMyBingoResponse> getMyBingo(User user) {
		return user.getTeam().getBingo().getCells().stream().map(cell -> {
			GetMyBingoResponse getMyBingoResponse = new GetMyBingoResponse();
			getMyBingoResponse.setMission(cell.getIsRevealed() ? cell.getMission().getDescription() : null);
			getMyBingoResponse.setIsComplete(cell.getIsComplete());
			getMyBingoResponse.setIsRevealed(cell.getIsRevealed());
			getMyBingoResponse.setId(cell.getId());
			return getMyBingoResponse;
		}).toList();
	}

	public String revealBingoCell(User user, Long id) {
		List<BingoCell> existsRevealedCells = user.getTeam()
			.getBingo()
			.getCells()
			.stream()
			.filter(BingoCell::getIsRevealed).toList();
		if (existsRevealedCells.isEmpty()) {
			BingoCell bingoCell = bingoCellRepository.findById(id)
				.orElseThrow(() -> new CustomException(ErrorCode.NULL_POINTER));
			bingoCell.setIsRevealed(true);
			return bingoCellRepository.save(bingoCell).getMission().getDescription();
		} else {
			if (existsRevealedCells.stream().filter(BingoCell::getIsComplete).count() == existsRevealedCells.size()) {
				BingoCell bingoCell = bingoCellRepository.findById(id)
					.orElseThrow(() -> new CustomException(ErrorCode.CELL_NOT_FOUND));
				bingoCell.setIsRevealed(true);
				return bingoCellRepository.save(bingoCell).getMission().getDescription();
			} else {
				throw new RuntimeException("이미 다른 열린 셀이 존재합니다.");
			}
		}
	}

	public void approveTeam(User user, Team team) {
		if (!user.getUserName().equals("오현우")) {
			log.error("누가 몰래 승인하려고 함");
			throw new CustomException(ErrorCode.PERMISSION_ERROR);
		}
		Bingo bingo = bingoRepository.findByTeam(team)
			.orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
		Optional<BingoCell> bingoCell = bingoCellRepository.findByBingoAndIsCompleteAndIsRevealed(bingo,
			false, true);
		if (bingoCell.isPresent()) {
			bingoCell.get().setIsComplete(true);
			bingoCellRepository.save(bingoCell.get());
		}
	}
}
