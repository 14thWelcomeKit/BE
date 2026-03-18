package com.likelion13th.Welcomekit_BE.repository.v1;

import com.likelion13th.Welcomekit_BE.domain.Team;
import com.likelion13th.Welcomekit_BE.domain.v1.BingoActivityLogV1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BingoActivityLogV1Repository extends JpaRepository<BingoActivityLogV1, Long> {

	List<BingoActivityLogV1> findByTeamOrderByTimestampDesc(Team team);
}
