package com.likelion13th.Welcomekit_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.likelion13th.Welcomekit_BE.domain.WelcomeKitPhoto;

@Repository
public interface WelcomeKitPhotoRepository extends JpaRepository<WelcomeKitPhoto, Long> {
}
