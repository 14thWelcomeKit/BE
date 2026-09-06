package com.likelion13th.Welcomekit_BE.manager;

import com.likelion13th.Welcomekit_BE.domain.Qna;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QnaManager {

    private final QnaRepository qnaRepository;

    public Qna save(Qna qna){
        return qnaRepository.save(qna);
    }

    public Page<Qna> findAll(Pageable pageable){
        return qnaRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
    }

    public Page<Qna> findAllByUser(User user, Pageable pageable){
        return qnaRepository.findByUserAndDeletedAtIsNullOrderByCreatedAtDesc(user, pageable);
    }

    public Optional<Qna> findById(Long id){
        return qnaRepository.findByIdAndDeletedAtIsNull(id);
    }

    public void delete(Qna qna){
        qna.setDeletedAt(LocalDateTime.now());
    }
}
