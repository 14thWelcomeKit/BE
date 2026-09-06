package com.likelion13th.Welcomekit_BE.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 1일 1회 배치로 계산되는 빙고 랭킹 스냅샷. 사용자당 한 행. */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BingoRanking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	/** 완료한 칸들의 미션 점수 합계 (단순 칸 개수 아님). */
	@Column(name = "score", nullable = false)
	private Integer score;

	/** 공동 순위 처리된 순위 (1,2,2,4 방식). */
	@Column(name = "rank_no", nullable = false)
	private Integer rank;

	/** 이 스냅샷이 계산된 배치 실행 시각 (같은 배치 내 모든 행이 동일한 값). */
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
}
