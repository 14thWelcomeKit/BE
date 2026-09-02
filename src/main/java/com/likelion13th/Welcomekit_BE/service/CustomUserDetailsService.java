package com.likelion13th.Welcomekit_BE.service;

import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService {

	private final UserRepository userRepository;

	public User findByUserName(String userName) {
		return userRepository.findByUserName(userName).orElse(null);
	}

	public User findByStudentNum(String studentNum) {
		return userRepository.findUserByStudentNum(studentNum).orElse(null);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}
}
