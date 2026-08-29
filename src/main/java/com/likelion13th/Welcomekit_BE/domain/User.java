package com.likelion13th.Welcomekit_BE.domain;

import com.likelion13th.Welcomekit_BE.domain.enums.DevPart;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;

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

@Entity(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "student_num", nullable = false)
	private String studentNum;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "user_type", nullable = false)
	private UserType userType;

	@Enumerated(EnumType.STRING)
	@Column(name = "dev_part", nullable = false)
	private DevPart devPart;

	@Column(name = "profile_image")
	private String profileImage;

	@Column(name = "has_read_welcome", nullable = false)
	private Boolean hasReadWelcome;

	@ManyToOne
	@JoinColumn(name = "team_id")
	private Team team;

	@Override
	public String toString() {
		return "User{" +
			"id=" + id +
			", userName='" + userName + '\'' +
			", password='" + password + '\'' +
			", studentNum='" + studentNum + '\'' +
			", email='" + email + '\'' +
			", userType=" + userType +
			", devPart=" + devPart +
			", profileImage='" + profileImage + '\'' +
			", hasReadWelcome=" + hasReadWelcome +
			", team=" + team +
			'}';
	}
}