package com.likelion13th.Welcomekit_BE.repository;

import com.likelion13th.Welcomekit_BE.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
