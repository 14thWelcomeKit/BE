package com.likelion13th.Welcomekit_BE.controller;

import com.likelion13th.Welcomekit_BE.domain.Qna;
import com.likelion13th.Welcomekit_BE.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qna")
@RequiredArgsConstructor
@Tag(name = "문의", description = "문의(QnA) 게시글 작성/목록/상세/삭제 API.")
public class QnaController {

    private final QnaService qnaService;

    @Operation(summary = "문의글 작성", description = "새 문의(QnA) 게시글을 작성합니다.")
    @PostMapping
    public Qna createQna(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String content
    ){
        return qnaService.createQna(userId, title, content);
    }

    @Operation(summary = "문의글 전체 목록 조회", description = "전체 문의글 목록을 조회합니다.")
    @GetMapping
    public List<Qna> getAllQna(){
        return qnaService.getAllQna();
    }

    @Operation(summary = "문의글 상세 조회", description = "특정 문의글의 상세 내용을 조회합니다.")
    @GetMapping("/{id}")
    public Qna getQna(@PathVariable Long id){
        return qnaService.getQna(id);
    }

    @Operation(summary = "문의글 삭제", description = "작성자 본인이 자신의 문의글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public void deleteQna(
            @PathVariable Long id,
            @RequestParam Long userId
    ){
        qnaService.deleteQna(id, userId);
    }
}