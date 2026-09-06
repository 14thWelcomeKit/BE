package com.likelion13th.Welcomekit_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.QnaComment;

public interface QnaCommentRepository extends JpaRepository<QnaComment, Long> {

    @EntityGraph(attributePaths = "user")
    List<QnaComment> findByQnaIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long qnaId);

    @EntityGraph(attributePaths = "user")
    Optional<QnaComment> findByIdAndDeletedAtIsNull(Long id);
}
