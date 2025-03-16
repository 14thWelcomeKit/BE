package com.likelion13th.Welcomekit_BE.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetAllBabyLionResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetMyInfoResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final PasswordEncoder passwordEncoder;

	public void createUser(CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUserName(createUserRequest.getName());
		user.setUserType(createUserRequest.getUserType());
		user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
		user.setStudentNum(createUserRequest.getStudentNum());
		userRepository.save(user);
	}

	public User getUserByStudentName(String studentName) {
		return userRepository.findUserByStudentNum(studentName)
			.orElseThrow(() -> new NotFoundException("해당 학번으로 존재하는 사람이 없습니다."));
	}

	public List<GetAllBabyLionResponse> getTotalBabyLion(User admin) {
		if (admin.getUserType() != UserType.ADMIN) {
			throw new RuntimeException("아기사자는 조회할수없습니다!");
		}
		log.info("아기사자 조회");
		return userRepository.findAll().stream().filter(user -> user.getUserType() == UserType.BABY_LION).map(user -> {
			GetAllBabyLionResponse babyLionResponse = new GetAllBabyLionResponse();
			babyLionResponse.setId(user.getId());
			babyLionResponse.setName(user.getUserName());
			babyLionResponse.setStudentNum(user.getStudentNum());
			babyLionResponse.setTeamName(user.getTeam() != null ? user.getTeam().getTeamName() : null);
			return babyLionResponse;
		}).toList();
	}

	public List<GetAllBabyLionResponse> getTotalAdmin(User admin) {
		if (admin.getUserType() != UserType.ADMIN) {
			throw new RuntimeException("아기사자는 조회할수없습니다!");
		}
		log.debug("운영진 조회");
		return userRepository.findAll().stream().filter(user -> user.getUserType() == UserType.ADMIN).map(user -> {
			GetAllBabyLionResponse babyLionResponse = new GetAllBabyLionResponse();
			babyLionResponse.setId(user.getId());
			babyLionResponse.setName(user.getUserName());
			babyLionResponse.setStudentNum(user.getStudentNum());
			babyLionResponse.setTeamName(user.getTeam() != null ? user.getTeam().getTeamName() : null);
			return babyLionResponse;
		}).toList();
	}

	public void save(User user) {
		userRepository.save(user);
	}

	public GetMyInfoResponse getMyInfo(User user) {
		GetMyInfoResponse getMyInfoResponse = new GetMyInfoResponse();
		getMyInfoResponse.setDevPart(user.getDevPart());
		getMyInfoResponse.setProfileImage(user.getProfileImage());
		getMyInfoResponse.setStudentName(user.getStudentNum());
		getMyInfoResponse.setName(user.getUserName());
		getMyInfoResponse.setTeamName(user.getTeam() != null ? user.getTeam().getTeamName() : null);
		return getMyInfoResponse;
	}
}
