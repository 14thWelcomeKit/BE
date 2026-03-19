package com.likelion13th.Welcomekit_BE.controller;

import com.likelion13th.Welcomekit_BE.domain.Qna;
import com.likelion13th.Welcomekit_BE.service.QnaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    @PostMapping
    public Qna createQna(
            @RequestParam Long userId,
            @RequestParam String title,
            @RequestParam String content
    ){
        return qnaService.createQna(userId, title, content);
    }

    @GetMapping
    public List<Qna> getAllQna(){
        return qnaService.getAllQna();
    }

    @GetMapping("/{id}")
    public Qna getQna(@PathVariable Long id){
        return qnaService.getQna(id);
    }

    @DeleteMapping("/{id}")
    public void deleteQna(@PathVariable Long id){
        qnaService.deleteQna(id);
    }
}