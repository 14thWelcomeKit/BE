package com.likelion13th.Welcomekit_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
	Optional<Team> findTeamByTeamName(String teamName);
}
