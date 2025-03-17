package com.likelion13th.Welcomekit_BE.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.BingoCell;

public interface BingoCellRepository extends JpaRepository<BingoCell, Long> {
}
