package com.likelion13th.Welcomekit_BE.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @GetMapping("/")
    @ResponseBody
    String hello(){
        return "메인페이지";
    }
}
