package com.likelion13th.Welcomekit_BE.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13th.Welcomekit_BE.domain.Qna;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateQnaRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.QnaDetailResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.QnaListResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.QnaSummaryResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;
import com.likelion13th.Welcomekit_BE.exception.QnaException;
import com.likelion13th.Welcomekit_BE.manager.QnaManager;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QnaService {

    private static final int DEFAULT_SIZE = 10;

    private final QnaManager qnaManager;
    private final UserRepository userRepository;

    @Transactional
    public QnaDetailResponse createQna(String email, CreateQnaRequest request){
        User user = getUser(email);

        Qna qna = Qna.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();

        Qna saved = qnaManager.save(qna);
        return toDetailResponse(saved, user);
    }

    @Transactional(readOnly = true)
    public QnaListResponse getQnaList(String email, int page, int size){
        User user = getUser(email);
        int safePage = Math.max(page, 0);
        int safeSize = size < 1 ? DEFAULT_SIZE : size;
        Pageable pageable = PageRequest.of(safePage, safeSize);

        // 운영진은 전체 문의글을, 일반 부원은 본인이 작성한 문의글만 조회할 수 있다.
        Page<Qna> result = user.getUserType() == UserType.ADMIN
                ? qnaManager.findAll(pageable)
                : qnaManager.findAllByUser(user, pageable);

        List<QnaSummaryResponse> qnas = result.getContent().stream()
                .map(this::toSummary)
                .toList();

        return QnaListResponse.builder()
                .qnas(qnas)
                .pageInfo(QnaListResponse.PageInfo.builder()
                        .page(safePage)
                        .size(safeSize)
                        .totalElements(result.getTotalElements())
                        .totalPages(result.getTotalPages())
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public QnaDetailResponse getQna(String email, Long id){
        User user = getUser(email);
        Qna qna = qnaManager.findById(id)
                .orElseThrow(() -> new QnaException(HttpStatus.NOT_FOUND, "E404", "존재하지 않는 문의글입니다."));

        assertViewable(qna, user);
        return toDetailResponse(qna, user);
    }

    @Transactional
    public void deleteQna(String email, Long qnaId){
        User user = getUser(email);
        Qna qna = qnaManager.findById(qnaId)
                .orElseThrow(() -> new QnaException(HttpStatus.NOT_FOUND, "E404", "존재하지 않는 문의글입니다."));

        if (!qna.getUser().getId().equals(user.getId())) {
            throw new QnaException(HttpStatus.FORBIDDEN, "E403", "본인의 문의글만 삭제할 수 있습니다.");
        }

        qnaManager.delete(qna);
        qnaManager.save(qna);
    }

    /** 본인 글이거나 운영진이면 조회 가능. QnaCommentService 에서도 댓글 작성/조회 권한 판단에 재사용한다. */
    void assertViewable(Qna qna, User user){
        if (user.getUserType() == UserType.ADMIN) {
            return;
        }
        if (!qna.getUser().getId().equals(user.getId())) {
            throw new QnaException(HttpStatus.FORBIDDEN, "E403", "본인의 문의글만 조회할 수 있습니다.");
        }
    }

    private User getUser(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new QnaException(HttpStatus.NOT_FOUND, "E404_USER", "사용자를 찾을 수 없습니다."));
    }

    private QnaSummaryResponse toSummary(Qna qna){
        return QnaSummaryResponse.builder()
                .qnaId(qna.getId())
                .title(qna.getTitle())
                .authorName(qna.getUser().getUserName())
                .createdAt(qna.getCreatedAt())
                .build();
    }

    private QnaDetailResponse toDetailResponse(Qna qna, User requester){
        return QnaDetailResponse.builder()
                .qnaId(qna.getId())
                .title(qna.getTitle())
                .content(qna.getContent())
                .authorId(qna.getUser().getId())
                .authorName(qna.getUser().getUserName())
                .createdAt(qna.getCreatedAt())
                .isOwner(qna.getUser().getId().equals(requester.getId()))
                .build();
    }
}
