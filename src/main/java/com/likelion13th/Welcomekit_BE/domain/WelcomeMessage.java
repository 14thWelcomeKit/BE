package com.likelion13th.Welcomekit_BE.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class WelcomeMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "receiver_id", nullable = false)
	private User receiver;

	@ManyToOne
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;

	@Column(name = "content", nullable = false, length = 500)
	private String content;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
}

