package com.likelion13th.Welcomekit_BE.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetAllBabyLionResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetMyInfoResponse;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;
import com.likelion13th.Welcomekit_BE.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserManager {
	@Autowired
	private final UserService userService;

	public void createUser(CreateUserRequest createUserRequest) {
		userService.createUser(createUserRequest);
	}

	public List<GetAllBabyLionResponse> getTotalBabyLion(UserDetails userDetails) {
		User user = userService.getUserByStudentNum(userDetails.getUsername());
		return userService.getTotalBabyLion(user);
	}

	public List<String> getTotalExceptMe(UserDetails userDetails) {
		return userService.getAllNameExceptMe(userDetails.getUsername());
	}

	public List<GetAllBabyLionResponse> getTotalAdmin(UserDetails userDetails) {
		User user = userService.getUserByStudentNum(userDetails.getUsername());
		return userService.getTotalAdmin(user);
	}

	public GetMyInfoResponse getMyInfo(UserDetails userDetails) {
		User user = userService.getUserByStudentNum(userDetails.getUsername());
		return userService.getMyInfo(user);
	}

	public void changePassword(UserDetails userDetails, String currentPassword, String newPassword) {
		User user = userService.getUserByStudentNum(userDetails.getUsername());
		userService.changePassword(user, currentPassword, newPassword);
	}

	public void saveProfileImage(MultipartFile file, UserDetails userDetails) {
		User user = userService.getUserByStudentNum(userDetails.getUsername());
		userService.saveProfileImage(file, user);
	}

	public Resource getProfileImage(UserDetails userDetails) {
		User user = userService.getUserByStudentNum(userDetails.getUsername());
		return userService.getProfileImage(user);
	}

	public String getProfileSource(Resource resource) {
		return userService.getProfileImageContentType(resource);
	}

}
