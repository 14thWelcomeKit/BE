package com.likelion13th.Welcomekit_BE.service;

import com.likelion13th.Welcomekit_BE.domain.*;
import com.likelion13th.Welcomekit_BE.manager.QnaCommentManager;
import com.likelion13th.Welcomekit_BE.manager.QnaManager;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaCommentService {

    private final QnaCommentManager commentManager;
    private final QnaManager qnaManager;
    private final UserRepository userRepository;

    public QnaComment createComment(Long userId, Long qnaId, String content){

        Qna qna = qnaManager.findById(qnaId)
                .orElseThrow(() -> new RuntimeException("Qna not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        QnaComment comment = QnaComment.builder()
                .content(content)
                .createdAt(LocalDateTime.now())
                .isAdminComment(false)
                .qna(qna)
                .user(user)
                .build();

        return commentManager.save(comment);
    }

    public List<QnaComment> getComments(Long qnaId){
        return commentManager.findByQnaId(qnaId);
    }


    public void deleteComment(Long commentId, Long userId){

        QnaComment comment = commentManager.findById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        comment.setDeletedAt(LocalDateTime.now());
    }
}