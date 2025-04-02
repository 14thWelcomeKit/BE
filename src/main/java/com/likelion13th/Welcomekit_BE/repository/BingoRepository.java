package com.likelion13th.Welcomekit_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.Bingo;
import com.likelion13th.Welcomekit_BE.domain.Team;

public interface BingoRepository extends JpaRepository<Bingo, Long> {
	Optional<Bingo> findByTeam(Team team);
}
