package com.likelion13th.Welcomekit_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.EmailVerification;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
	Optional<EmailVerification> findByEmail(String email);
}
