package com.likelion13th.Welcomekit_BE.service;

import com.likelion13th.Welcomekit_BE.domain.Qna;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.manager.QnaManager;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaManager qnaManager;
    private final UserRepository userRepository;

    public Qna createQna(Long userId, String title, String content){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Qna qna = Qna.builder()
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        return qnaManager.save(qna);
    }

    public List<Qna> getAllQna(){
        return qnaManager.findAll();
    }

    public Qna getQna(Long id){
        return qnaManager.findById(id)
                .orElseThrow(() -> new RuntimeException("Qna not found"));
    }


    public void deleteQna(Long qnaId, Long userId){

        Qna qna = qnaManager.findById(qnaId)
                .orElseThrow(() -> new RuntimeException("Qna not found"));

        if (!qna.getUser().getId().equals(userId)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        qnaManager.delete(qna);
    }
}