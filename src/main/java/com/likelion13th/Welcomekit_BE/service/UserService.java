package com.likelion13th.Welcomekit_BE.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final PasswordEncoder passwordEncoder;

	public void createUser(CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUserName(createUserRequest.getName());
		user.setUserType(createUserRequest.getUserType());
		user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
		user.setStudentNum(createUserRequest.getStudentNum());
		userRepository.save(user);
	}
}
