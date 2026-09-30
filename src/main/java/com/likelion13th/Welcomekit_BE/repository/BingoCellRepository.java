package com.likelion13th.Welcomekit_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion13th.Welcomekit_BE.domain.Bingo;
import com.likelion13th.Welcomekit_BE.domain.BingoCell;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.enums.BingoCellStatus;

import jakarta.persistence.LockModeType;

public interface BingoCellRepository extends JpaRepository<BingoCell, Long> {
	Optional<BingoCell> findByBingoAndPosition(Bingo bingo, Integer position);

	/**
	 * PENDING 취소 시 비관적 쓰기 잠금.
	 * 상대의 매칭 완료 트랜잭션과 직렬화해 한쪽만 COMPLETED 로 남는 불일치를 막는다.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c FROM BingoCell c WHERE c.bingo = :bingo AND c.position = :position")
	Optional<BingoCell> findByBingoAndPositionWithPessimisticLock(@Param("bingo") Bingo bingo,
		@Param("position") Integer position);

	List<BingoCell> findByBingoAndMatchedUser(Bingo bingo, User matchedUser);

	boolean existsByBingoAndMatchedUserAndStatus(Bingo bingo, User matchedUser, BingoCellStatus status);
}
