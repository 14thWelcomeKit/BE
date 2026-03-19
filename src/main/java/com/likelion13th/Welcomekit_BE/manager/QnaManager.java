package com.likelion13th.Welcomekit_BE.manager;

import com.likelion13th.Welcomekit_BE.domain.Qna;
import com.likelion13th.Welcomekit_BE.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        return qnaRepository.findAll();
    }

    public Optional<Qna> findById(Long id){
        return qnaRepository.findById(id);
    }

    public void delete(Long id){
        qnaRepository.deleteById(id);
    }
}