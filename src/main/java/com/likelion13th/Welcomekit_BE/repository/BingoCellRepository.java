package com.likelion13th.Welcomekit_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.Bingo;
import com.likelion13th.Welcomekit_BE.domain.BingoCell;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.enums.BingoCellStatus;

public interface BingoCellRepository extends JpaRepository<BingoCell, Long> {
	Optional<BingoCell> findByBingoAndPosition(Bingo bingo, Integer position);

	List<BingoCell> findByBingoAndMatchedUser(Bingo bingo, User matchedUser);

	boolean existsByBingoAndMatchedUserAndStatus(Bingo bingo, User matchedUser, BingoCellStatus status);
}
