package com.likelion13th.Welcomekit_BE.domain.v1;

import com.likelion13th.Welcomekit_BE.domain.Team;
import com.likelion13th.Welcomekit_BE.domain.enums.v1.ActivityLogStatusV1;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bingo_activity_log_v1")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BingoActivityLogV1 {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cell_id", nullable = false)
	private BingoCellV1 cell;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private ActivityLogStatusV1 status;

	@Column(name = "message", nullable = false)
	private String message;

	@Column(name = "timestamp", nullable = false)
	private LocalDateTime timestamp;
}
