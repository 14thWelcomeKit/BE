package com.likelion13th.Welcomekit_BE.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.likelion13th.Welcomekit_BE.domain.BingoCell;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetMyBingoResponse;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.repository.BingoCellRepository;
import com.likelion13th.Welcomekit_BE.repository.BingoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BingoService {
	@Autowired
	private final BingoRepository bingoRepository;
	@Autowired
	private final BingoCellRepository bingoCellRepository;

	public List<GetMyBingoResponse> getMyBingo(User user) {
		return user.getTeam().getBingo().getCells().stream().map(cell -> {
			GetMyBingoResponse getMyBingoResponse = new GetMyBingoResponse();
			getMyBingoResponse.setMission(cell.isRevealed() ? cell.getMission().getDescription() : null);
			getMyBingoResponse.setIsComplete(cell.isComplete());
			getMyBingoResponse.setIsRevealed(cell.isRevealed());
			getMyBingoResponse.setId(cell.getId());
			return getMyBingoResponse;
		}).toList();
	}

	public String revealBingoCell(User user, Long id) {
		List<BingoCell> existsRevealedCells = user.getTeam()
			.getBingo()
			.getCells()
			.stream()
			.filter(BingoCell::isRevealed).toList();
		if (existsRevealedCells.isEmpty()) {
			BingoCell bingoCell = bingoCellRepository.findById(id)
				.orElseThrow(() -> new CustomException(ErrorCode.NULL_POINTER));
			bingoCell.setRevealed(true);
			return bingoCellRepository.save(bingoCell).getMission().getDescription();
		} else {
			if (existsRevealedCells.stream().filter(BingoCell::isComplete).count() == existsRevealedCells.size()) {
				BingoCell bingoCell = bingoCellRepository.findById(id)
					.orElseThrow(() -> new CustomException(ErrorCode.CELL_NOT_FOUND));
				bingoCell.setRevealed(true);
				return bingoCellRepository.save(bingoCell).getMission().getDescription();
			} else {
				throw new RuntimeException("이미 다른 열린 셀이 존재합니다.");
			}
		}
	}
}
