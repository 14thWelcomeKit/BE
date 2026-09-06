package com.likelion13th.Welcomekit_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.BingoRanking;
import com.likelion13th.Welcomekit_BE.domain.User;

public interface BingoRankingRepository extends JpaRepository<BingoRanking, Long> {
	Optional<BingoRanking> findByUser(User user);

	List<BingoRanking> findByRankLessThanEqualOrderByRankAsc(Integer rank);
}
