package com.likelion13th.Welcomekit_BE.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.manager.UserManager;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private final UserManager userManager;

    @PostMapping("/join")
    ResponseEntity<?> createUser(@RequestBody CreateUserRequest createUserRequest) {
        userManager.createUser(createUserRequest);
        return ResponseEntity.ok("성공적으로 생성했습니다!");
    }
}
