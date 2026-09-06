package com.likelion13th.Welcomekit_BE.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

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
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "code", nullable = false, unique = true, length = 4)
	private String code;

	@OneToMany(mappedBy = "bingo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BingoCell> cells;
}
