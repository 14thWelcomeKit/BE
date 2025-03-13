package com.likelion13th.Welcomekit_BE.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.LoginRequest;
import com.likelion13th.Welcomekit_BE.jwt.JwtAuthenticationResponse;
import com.likelion13th.Welcomekit_BE.jwt.JwtTokenProvider;
import com.likelion13th.Welcomekit_BE.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthManager {

	@Autowired
	private final AuthService authService;
	@Autowired
	private final JwtTokenProvider tokenProvider;
	@Autowired
	private final PasswordEncoder passwordEncoder;

	public JwtAuthenticationResponse authenticateUser(@Valid LoginRequest loginRequest) {
		try {
			User user = authService.findByStudentNum(loginRequest.getStudentNum());
			if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
				throw new BadCredentialsException("Invalid credentials. Please check your username and password.");
			}
			String jwt = tokenProvider.generateToken(user);
			return new JwtAuthenticationResponse(jwt);
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException("Invalid credentials. Please check your username and password.");
		} catch (UsernameNotFoundException e) {
			throw new UsernameNotFoundException("User Not Found. Please sign up first.");
		} catch (Exception e) {
			throw new RuntimeException("Internal server error. Please try again later.");
		}
	}
}