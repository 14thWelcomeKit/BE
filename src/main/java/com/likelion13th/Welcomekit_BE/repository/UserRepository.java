package com.likelion13th.Welcomekit_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion13th.Welcomekit_BE.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserName(String userName);
	Optional<User> findUserByStudentNum(String studentNum);
}
