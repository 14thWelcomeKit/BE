package com.likelion13th.Welcomekit_BE.domain;

import java.time.LocalDateTime;

import com.likelion13th.Welcomekit_BE.domain.enums.BingoCellStatus;
import com.likelion13th.Welcomekit_BE.domain.enums.BingoEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BingoCell {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "bingo_id", nullable = false)
	private Bingo bingo;

	/** 보드 상의 위치 (1~25). 응답의 cellId 로 그대로 사용된다. */
	@Column(name = "position", nullable = false)
	private Integer position;

	@Enumerated(EnumType.STRING)
	@Column(name = "mission", nullable = false)
	private BingoEnum mission;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private BingoCellStatus status;

	/** PENDING 이면 인증을 기다리는 상대, COMPLETED 면 함께 매칭을 완료한 상대. */
	@ManyToOne
	@JoinColumn(name = "matched_user_id")
	private User matchedUser;

	/** PENDING 상태의 48시간 만료 시각. PENDING 이 아니면 null. */
	@Column(name = "pending_expires_at")
	private LocalDateTime pendingExpiresAt;
}
