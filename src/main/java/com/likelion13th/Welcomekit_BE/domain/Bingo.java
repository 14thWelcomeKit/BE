package com.likelion13th.Welcomekit_BE.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

import com.likelion13th.Welcomekit_BE.domain.enums.BingoEnum;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bingo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@OneToOne
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;

	@OneToMany(mappedBy = "bingo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BingoCell> cells;
}

