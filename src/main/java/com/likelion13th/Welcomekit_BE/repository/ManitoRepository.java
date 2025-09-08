package com.likelion13th.Welcomekit_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.Manito;
import com.likelion13th.Welcomekit_BE.domain.User;

public interface ManitoRepository extends JpaRepository<Manito, Long> {
	Manito findByUser(User user);

	Manito findByManito(User manito);
}
