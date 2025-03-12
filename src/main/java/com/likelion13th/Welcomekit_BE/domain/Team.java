package com.likelion13th.Welcomekit_BE.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Team {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "team_name", nullable = false, unique = true)
	private String teamName;

	// 팀장 (1명, 운영진)
	@OneToOne
	@JoinColumn(name = "leader_id")
	private User leader;

	// 운영진 (최대 2명)
	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
	private List<User> executives;

	// 일반 유저 (최대 4명)
	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
	private List<User> members;
}