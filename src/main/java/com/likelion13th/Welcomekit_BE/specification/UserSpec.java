package com.likelion13th.Welcomekit_BE.specification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.LoginRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

public interface UserSpec {

	@Operation(
		summary = "사용자 로그인",
		description = "사용자가 로그인하여 JWT 토큰을 발급받습니다.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "로그인 요청 예제",
					value = """
						{
						  "userId": "testuser",
						  "password": "password123"
						}
						"""
				)
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "로그인 성공",
				content = @Content(
					mediaType = "application/json",
					examples = @ExampleObject(
						name = "성공 응답 예제",
						value = """
							{
							  "accessToken": "eyJhbGciOiJIUzI1...",
							  "tokenType": "Bearer"
							}
							"""
					)
				)
			),
			@ApiResponse(
				responseCode = "401",
				description = "잘못된 아이디 또는 비밀번호",
				content = @Content(
					mediaType = "application/json",
					examples = @ExampleObject(
						name = "실패 응답 예제",
						value = """
							{
							  "message": "Invalid credentials. Please check your username and password."
							}
							"""
					)
				)
			),
			@ApiResponse(
				responseCode = "500",
				description = "서버 내부 오류",
				content = @Content(
					mediaType = "application/json",
					examples = @ExampleObject(
						name = "실패 응답 예제",
						value = """
							{
							  "message": "Internal server error. Please try again later."
							}
							"""
					)
				)
			)
		}
	)
	@Tag(name = "user", description = "유저 관련 API")
	ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest);
}
