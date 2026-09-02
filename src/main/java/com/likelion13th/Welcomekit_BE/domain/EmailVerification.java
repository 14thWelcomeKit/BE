package com.likelion13th.Welcomekit_BE.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원가입 시 학교 이메일 인증을 위한 엔티티.
 * 이메일 1건당 최신 인증코드/만료시각/인증여부를 관리한다.
 */
@Entity(name = "email_verification")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailVerification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "verified", nullable = false)
	private boolean verified;

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}
}
