package com.likelion13th.Welcomekit_BE.controller;

import com.likelion13th.Welcomekit_BE.domain.QnaComment;
import com.likelion13th.Welcomekit_BE.service.QnaCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qna/comments")
@RequiredArgsConstructor
@Tag(name = "문의 댓글", description = "문의(QnA) 게시글에 대한 댓글 작성/조회/삭제 API.")
public class QnaCommentController {

    private final QnaCommentService service;

    @Operation(summary = "문의 댓글 작성", description = "특정 문의글에 댓글을 작성합니다.")
    @PostMapping
    public QnaComment create(
            @RequestParam Long userId,
            @RequestParam Long qnaId,
            @RequestParam String content
    ){
        return service.createComment(userId, qnaId, content);
    }

    @Operation(summary = "문의 댓글 목록 조회", description = "특정 문의글의 댓글 목록을 조회합니다.")
    @GetMapping("/{qnaId}")
    public List<QnaComment> get(@PathVariable Long qnaId){
        return service.getComments(qnaId);
    }

    @Operation(summary = "문의 댓글 삭제", description = "작성자 본인이 자신의 댓글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestParam Long userId
    ){
        service.deleteComment(id, userId);
    }
}