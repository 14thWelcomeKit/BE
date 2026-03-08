package com.likelion13th.Welcomekit_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.WelcomeMessage;

public interface WelcomeMessageRepository extends JpaRepository<WelcomeMessage, Long> {

	Optional<WelcomeMessage> findTopByReceiverOrderByCreatedAtDesc(User receiver);
}

