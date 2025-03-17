package com.likelion13th.Welcomekit_BE.domain;

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

	@Enumerated(EnumType.STRING)
	@Column(name = "mission", nullable = false)
	private BingoEnum mission;

	@Column(name = "is_complete", nullable = false)
	private boolean isComplete;

	@Column(name = "is_revealed", nullable = false)
	private boolean isRevealed;
}
