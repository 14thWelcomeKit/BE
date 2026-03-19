package com.likelion13th.Welcomekit_BE.repository.v1;

import com.likelion13th.Welcomekit_BE.domain.Team;
import com.likelion13th.Welcomekit_BE.domain.enums.v1.BingoCellStatusV1;
import com.likelion13th.Welcomekit_BE.domain.v1.BingoCellV1;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BingoCellV1Repository extends JpaRepository<BingoCellV1, Long> {

	List<BingoCellV1> findAllByOrderByIdAsc();

	List<BingoCellV1> findByStatus(BingoCellStatusV1 status);

	long countByOwnerTeamAndStatus(Team team, BingoCellStatusV1 status);

	/**
	 * 업로드/수정 시 동시성 제어용 비관적 쓰기 잠금.
	 * 같은 셀에 대한 동시 업로드를 직렬화하여 레이스 컨디션을 방지한다.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c FROM BingoCellV1 c WHERE c.id = :id")
	Optional<BingoCellV1> findByIdWithPessimisticLock(@Param("id") Long id);
}
