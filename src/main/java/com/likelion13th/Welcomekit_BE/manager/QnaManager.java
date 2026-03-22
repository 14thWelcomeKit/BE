package com.likelion13th.Welcomekit_BE.manager;

import com.likelion13th.Welcomekit_BE.domain.Qna;
import com.likelion13th.Welcomekit_BE.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QnaManager {

    private final QnaRepository qnaRepository;

    public Qna save(Qna qna){
        return qnaRepository.save(qna);
    }

    public List<Qna> findAll(){
        return qnaRepository.findByDeletedAtIsNull();
    }

    public Optional<Qna> findById(Long id){
        return qnaRepository.findById(id)
                .filter(q -> q.getDeletedAt() == null);
    }

    public void delete(Qna qna){
        qna.setDeletedAt(LocalDateTime.now());
    }
}