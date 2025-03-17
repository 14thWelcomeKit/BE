package com.likelion13th.Welcomekit_BE.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetMyBingoResponse;
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
			getMyBingoResponse.setMission(cell.isRevealed() ? cell.getMission() : null);
			getMyBingoResponse.setIsComplete(cell.isComplete());
			getMyBingoResponse.setIsRevealed(cell.isRevealed());
			getMyBingoResponse.setId(cell.getId());
			return getMyBingoResponse;
		}).toList();
	}

	
}
