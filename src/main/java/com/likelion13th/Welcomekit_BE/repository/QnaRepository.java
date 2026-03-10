package com.likelion13th.Welcomekit_BE.repository;

import com.likelion13th.Welcomekit_BE.domain.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaRepository extends JpaRepository<Qna,Long> {

}
