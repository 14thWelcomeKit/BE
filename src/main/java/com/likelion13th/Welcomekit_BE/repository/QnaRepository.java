package com.likelion13th.Welcomekit_BE.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.likelion13th.Welcomekit_BE.domain.Qna;
import com.likelion13th.Welcomekit_BE.domain.User;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {

    @EntityGraph(attributePaths = "user")
    Page<Qna> findByDeletedAtIsNullOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Page<Qna> findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(User user, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Optional<Qna> findByIdAndDeletedAtIsNull(Long id);
}
