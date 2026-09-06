package com.likelion13th.Welcomekit_BE.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13th.Welcomekit_BE.domain.Qna;
import com.likelion13th.Welcomekit_BE.domain.QnaComment;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateQnaCommentRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.QnaCommentResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;
import com.likelion13th.Welcomekit_BE.exception.QnaException;
import com.likelion13th.Welcomekit_BE.manager.QnaCommentManager;
import com.likelion13th.Welcomekit_BE.manager.QnaManager;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QnaCommentService {

    private final QnaCommentManager commentManager;
    private final QnaManager qnaManager;
    private final QnaService qnaService;
    private final UserRepository userRepository;

    @Transactional
    public QnaCommentResponse createComment(String email, CreateQnaCommentRequest request){
        User user = getUser(email);
        Qna qna = qnaManager.findById(request.getQnaId())
                .orElseThrow(() -> new QnaException(HttpStatus.NOT_FOUND, "E404", "존재하지 않는 문의글입니다."));

        qnaService.assertViewable(qna, user);

        QnaComment comment = QnaComment.builder()
                .content(request.getContent())
                .isAdminComment(user.getUserType() == UserType.ADMIN)
                .qna(qna)
                .user(user)
                .build();

        QnaComment saved = commentManager.save(comment);
        return toResponse(saved, user);
    }

    @Transactional(readOnly = true)
    public List<QnaCommentResponse> getComments(String email, Long qnaId){
        User user = getUser(email);
        Qna qna = qnaManager.findById(qnaId)
                .orElseThrow(() -> new QnaException(HttpStatus.NOT_FOUND, "E404", "존재하지 않는 문의글입니다."));

        qnaService.assertViewable(qna, user);

        return commentManager.findByQnaId(qnaId).stream()
                .map(comment -> toResponse(comment, user))
                .toList();
    }

    @Transactional
    public void deleteComment(String email, Long commentId){
        User user = getUser(email);
        QnaComment comment = commentManager.findById(commentId);

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new QnaException(HttpStatus.FORBIDDEN, "E403", "본인의 댓글만 삭제할 수 있습니다.");
        }

        comment.setDeletedAt(LocalDateTime.now());
        commentManager.save(comment);
    }

    private User getUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new QnaException(HttpStatus.NOT_FOUND, "E404_USER", "사용자를 찾을 수 없습니다."));
    }

    private QnaCommentResponse toResponse(QnaComment comment, User requester){
        return QnaCommentResponse.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getUser().getId())
                .authorName(comment.getUser().getUserName())
                .isAdminComment(Boolean.TRUE.equals(comment.getIsAdminComment()))
                .isOwner(comment.getUser().getId().equals(requester.getId()))
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
