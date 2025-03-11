package com.likelion13th.Welcomekit_BE.controller;

import com.likelion13th.Welcomekit_BE.domain.Member;
import com.likelion13th.Welcomekit_BE.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository MemberRepository;

    @GetMapping("/register")
    String register() {
        return "register.html";
    }

    @PostMapping("/member")
    String addMember(String name,String studentnumber, String studentid, String password) {


        Member member = new Member();
        member.setName(name);
        member.setStudentnumber(studentnumber);
        member.setStudentid(studentid);
        member.setPassword(new BCryptPasswordEncoder().encode(password));
        MemberRepository.save(member);
        return "redirect:/";

    }
}
