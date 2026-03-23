package com.likelion13th.Welcomekit_BE.controller;

import com.likelion13th.Welcomekit_BE.domain.QnaComment;
import com.likelion13th.Welcomekit_BE.service.QnaCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qna/comments")
@RequiredArgsConstructor
public class QnaCommentController {

    private final QnaCommentService service;

    @PostMapping
    public QnaComment create(
            @RequestParam Long userId,
            @RequestParam Long qnaId,
            @RequestParam String content
    ){
        return service.createComment(userId, qnaId, content);
    }

    @GetMapping("/{qnaId}")
    public List<QnaComment> get(@PathVariable Long qnaId){
        return service.getComments(qnaId);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @RequestParam Long userId
    ){
        service.deleteComment(id, userId);
    }
}