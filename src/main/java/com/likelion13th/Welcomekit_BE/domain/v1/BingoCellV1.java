package com.likelion13th.Welcomekit_BE.domain.v1;

import com.likelion13th.Welcomekit_BE.domain.Team;
import com.likelion13th.Welcomekit_BE.domain.enums.v1.BingoCellStatusV1;
import com.likelion13th.Welcomekit_BE.domain.enums.v1.BingoMissionV1;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bingo_cell_v1")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BingoCellV1 {

	/**
	 * 1~25 고정 ID: 보드 상의 위치를 의미. auto-increment 사용 안 함.
	 * BingoBoardInitializer 가 애플리케이션 기동 시 1회 생성.
	 */
	@Id
	@Column(name = "id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "mission", nullable = false)
	private BingoMissionV1 mission;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private BingoCellStatusV1 status;

	/** 현재 도전 중이거나 점유한 팀. EMPTY 이면 null. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_team_id")
	private Team ownerTeam;

	/** 로컬 파일 경로. EMPTY 이면 null. */
	@Column(name = "image_path")
	private String imagePath;

	/** PROCESSING 상태가 시작된 시각. 만료 기준 = occupiedAt + 12h */
	@Column(name = "occupied_at")
	private LocalDateTime occupiedAt;
}
