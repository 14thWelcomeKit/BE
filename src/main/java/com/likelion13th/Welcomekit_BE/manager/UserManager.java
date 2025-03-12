package com.likelion13th.Welcomekit_BE.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManager {
	@Autowired
	private final UserService userService;

	public void createUser(CreateUserRequest createUserRequest) {
		userService.createUser(createUserRequest);
	}
}
