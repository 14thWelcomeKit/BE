package com.likelion13th.Welcomekit_BE.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.LoginRequest;
import com.likelion13th.Welcomekit_BE.jwt.JwtAuthenticationResponse;
import com.likelion13th.Welcomekit_BE.manager.AuthManager;
import com.likelion13th.Welcomekit_BE.specification.UserSpec;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

	@Autowired
	private final AuthManager authManager;

	@PostMapping("/sign-in")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		JwtAuthenticationResponse jwtAuthenticationResponse = authManager.authenticateUser(loginRequest);
		return ResponseEntity.ok(jwtAuthenticationResponse);
	}

}
