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
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "team_name", nullable = false, unique = true)
	private String teamName;

	@OneToOne(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
	private Bingo bingo;

	@OneToOne
	@JoinColumn(name = "leader_id")
	private User leader;

	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
	private List<User> executives;

	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
	private List<User> members;
}
