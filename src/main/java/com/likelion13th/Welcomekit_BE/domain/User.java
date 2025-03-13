package com.likelion13th.Welcomekit_BE.domain;

import com.likelion13th.Welcomekit_BE.domain.enums.UserType;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "student_num", nullable = false)
	private String studentNum;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_type", nullable = false)
	private UserType userType;

	@ManyToOne
	@JoinColumn(name = "team_id")
	private Team team;
}