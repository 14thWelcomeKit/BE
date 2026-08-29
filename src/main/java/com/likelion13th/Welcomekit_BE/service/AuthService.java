package com.likelion13th.Welcomekit_BE.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	@Autowired
	private final UserRepository userRepository;

	public User findByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
	}

	public boolean existsByStudentNum(String userId) {
		return userRepository.existsByStudentNum(userId);
	}

	public void save(User user) {
		userRepository.save(user);
	}

}
