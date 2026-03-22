package com.likelion13th.Welcomekit_BE.repository;

import com.likelion13th.Welcomekit_BE.domain.QnaComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaCommentRepository extends JpaRepository<QnaComment, Long> {

    List<QnaComment> findByQnaIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long qnaId);
}