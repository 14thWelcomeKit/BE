package com.likelion13th.Welcomekit_BE.manager;

import com.likelion13th.Welcomekit_BE.domain.QnaComment;
import com.likelion13th.Welcomekit_BE.repository.QnaCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QnaCommentManager {

    private final QnaCommentRepository repository;

    public QnaComment save(QnaComment comment){
        return repository.save(comment);
    }

    public List<QnaComment> findByQnaId(Long qnaId){
        return repository.findByQnaIdAndDeletedAtIsNull(qnaId);
    }

    public QnaComment findById(Long id){
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
    }
}