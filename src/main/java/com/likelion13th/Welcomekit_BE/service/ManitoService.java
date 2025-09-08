package com.likelion13th.Welcomekit_BE.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.likelion13th.Welcomekit_BE.domain.Manito;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;
import com.likelion13th.Welcomekit_BE.repository.ManitoRepository;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ManitoService {
	private final ManitoRepository manitoRepository;
	private final UserRepository userRepository;

	@Transactional
	public void createManitoAssignments() {
		// 1. 모든 활성 사용자 조회
		List<User> users = userRepository.findAll();

		// 2. 기존 마니또 관계 삭제 (재설정 시)
		manitoRepository.deleteAll();

		// 3. 사용자 목록을 셔플
		List<User> shuffledUsers = new ArrayList<>(users);
		Collections.shuffle(shuffledUsers, new SecureRandom());

		// 4. 순환 방식으로 마니또 배정
		List<Manito> manitoList = new ArrayList<>();
		for (int i = 0; i < shuffledUsers.size(); i++) {
			User giver = shuffledUsers.get(i);
			User receiver = shuffledUsers.get((i + 1) % shuffledUsers.size());

			Manito manito = Manito.builder()
				.user(giver)        // 마니또를 해주는 사람
				.manito(receiver)   // 마니또를 받는 사람
				.build();

			manitoList.add(manito);
		}
		manitoRepository.saveAll(manitoList);
	}

	public String getManito(String userName) {
		log.info("마니또 조회 기능 실행중 학번 : {}", userName);
		User user = userRepository.findUserByStudentNum(userName).orElseThrow(() -> new NotFoundException("유저가 없어요"));
		Manito manito = manitoRepository.findByUser(user);
		if (manito == null) {
			return "아직 마니또 생성 전입니다!";
		}
		return manito.getManito().getUserName();
	}

	public void deleteManito(String studentNum) {
		User user = userRepository.findUserByStudentNum(studentNum).orElseThrow(() -> new NotFoundException("유저가 없어요"));
		if (user.getUserType() == UserType.ADMIN) {
			if (user.getUserName().equals("오현우") || user.getUserName().equals("신상현") || user.getUserName()
				.equals("오혀누")) {
				manitoRepository.deleteAll();
			}
		}
	}
}
