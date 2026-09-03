package com.likelion13th.Welcomekit_BE.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetAllBabyLionResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetMyInfoResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
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

	@Autowired
	private final EmailVerificationService emailVerificationService;

	@Value("${app.signup.admin-invite-code:}")
	private String adminInviteCode;

	public void createUser(CreateUserRequest createUserRequest) {
		String email = createUserRequest.getEmail();

		// 1) 이메일 인증 완료 여부 확인 (인증 안 됐으면 가입 불가)
		emailVerificationService.assertVerified(email);

		// 2) 중복 검사
		if (userRepository.existsByEmail(email)) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
		if (userRepository.existsByStudentNum(createUserRequest.getStudentNum())) {
			throw new CustomException(ErrorCode.STUDENT_NUM_ALREADY_EXISTS);
		}

		// 3) 권한 결정: 클라이언트가 보낸 userType은 신뢰하지 않는다.
		//    올바른 운영진 초대코드를 입력한 경우에만 ADMIN, 그 외에는 무조건 BABY_LION.
		UserType userType = resolveUserType(createUserRequest.getInviteCode());

		User user = new User();
		user.setUserName(createUserRequest.getName());
		user.setUserType(userType);
		user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
		user.setStudentNum(createUserRequest.getStudentNum());
		user.setEmail(email);
		user.setDevPart(createUserRequest.getDevPart());
		user.setProfileImage("");
		user.setHasReadWelcome(false);
		userRepository.save(user);
	}

	private UserType resolveUserType(String inviteCode) {
		if (inviteCode == null || inviteCode.isBlank()) {
			return UserType.BABY_LION;
		}
		// 초대코드를 입력했는데 설정값과 다르면 오류(오타로 인한 조용한 BABY_LION 가입 방지)
		if (adminInviteCode == null || adminInviteCode.isBlank() || !adminInviteCode.equals(inviteCode)) {
			throw new CustomException(ErrorCode.INVALID_INVITE_CODE);
		}
		return UserType.ADMIN;
	}

	/**
	 * 운영진 승격: 이미 ADMIN인 사용자가 특정 유저를 ADMIN으로 올린다.
	 * (초대코드 방식의 백업 수단)
	 */
	public void promoteToAdmin(User requester, Long targetUserId) {
		if (requester.getUserType() != UserType.ADMIN) {
			log.error("운영진 승격 권한 없음");
			throw new CustomException(ErrorCode.PERMISSION_ERROR);
		}
		User target = userRepository.findById(targetUserId)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
		target.setUserType(UserType.ADMIN);
		userRepository.save(target);
	}

	public User getUserByStudentNum(String studentNum) {
		return userRepository.findUserByStudentNum(studentNum)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	// 로그인 principal(이메일)로 유저 조회
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	public List<GetAllBabyLionResponse> getTotalBabyLion(User admin) {
		if (admin.getUserType() != UserType.ADMIN) {
			log.error("관리자만 조회할수있습니다.");
			throw new CustomException(ErrorCode.PERMISSION_ERROR);
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

	public List<String> getAllNameExceptMe(String email) {
		return userRepository.findAll()
			.stream()
			.filter(user -> !user.getEmail().equals(email))
			.map(User::getUserName)
			.toList();
	}

	public List<User> getTotalBabyLionUser() {
		return userRepository.findAll().stream().filter(user -> user.getUserType() == UserType.BABY_LION).toList();
	}

	public List<GetAllBabyLionResponse> getTotalAdmin(User admin) {
		if (admin.getUserType() != UserType.ADMIN) {
			log.error("관리자만 조회할수있습니다.");
			throw new CustomException(ErrorCode.PERMISSION_ERROR);
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
		getMyInfoResponse.setHasReadWelcome(user.getHasReadWelcome());
		return getMyInfoResponse;
	}

	public void updateHasReadWelcome(User user, boolean hasReadWelcome) {
		user.setHasReadWelcome(hasReadWelcome);
		userRepository.save(user);
	}

	public void changePassword(User user, String currentPassword, String newPassword) {
		if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
			log.error("비밀번호가 틀렸습니다.");
			throw new CustomException(ErrorCode.PASSWORD_NOT_MATCHES);
		}
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	/**
	 * 비밀번호 재설정(찾기): 로그인 없이, 이메일 인증코드 검증이 완료된 상태에서만 새 비밀번호로 재설정한다.
	 * - 이메일 인증 완료(verified) + 미만료 상태를 강제 확인
	 * - 현재 비밀번호는 요구하지 않음(비밀번호를 잊은 상황이므로)
	 */
	public void resetPassword(String email, String newPassword) {
		// 1) 이메일 인증 완료 여부 확인 (verify-code 통과 안 했으면 재설정 불가)
		emailVerificationService.assertVerified(email);

		// 2) 대상 사용자 조회
		User user = getUserByEmail(email);

		// 3) 새 비밀번호로 갱신
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	public void saveProfileImage(MultipartFile file, User user) {
		String studentNum = user.getStudentNum();
		Path uploadDir = Paths.get("/app/external-profile/" + studentNum);

		try {
			if (!Files.exists(uploadDir)) {
				Files.createDirectories(uploadDir);
			}

			// 확장자 고정 (예: JPG)
			String fileName = "profile.jpg"; // 또는 "profile.webp"
			Path filePath = uploadDir.resolve(fileName);

			// 파일 이미 존재하면 삭제 (덮어쓰기 목적)
			if (Files.exists(filePath)) {
				Files.delete(filePath);
			}

			// BufferedImage로 읽기
			BufferedImage originalImage = ImageIO.read(file.getInputStream());
			if (originalImage == null) {
				log.error("Image가 null입니다.");
				throw new CustomException(ErrorCode.INVALID_IMAGE_FORMAT);
			}

			// 이미지 리사이즈 (최대 300px)
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();
			int maxSize = 300;
			float scale = Math.min((float)maxSize / width, (float)maxSize / height);
			int newWidth = Math.round(width * scale);
			int newHeight = Math.round(height * scale);

			Image resized = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
			BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = resizedImage.createGraphics();
			g.drawImage(resized, 0, 0, null);
			g.dispose();

			// 압축해서 저장 (JPEG 예시)
			try (OutputStream os = Files.newOutputStream(filePath)) {
				ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
				ImageOutputStream ios = ImageIO.createImageOutputStream(os);
				writer.setOutput(ios);

				ImageWriteParam param = writer.getDefaultWriteParam();
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(0.7f); // 70% 퀄리티로 압축

				writer.write(null, new IIOImage(resizedImage, null, null), param);
				writer.dispose();
				ios.close();
			}

			// DB 경로 저장
			String filePath2 = "/app/external-profile/" + studentNum + "/" + fileName;
			user.setProfileImage(filePath2);

			userRepository.save(user);

		} catch (IOException e) {
			log.error("프로필 이미지 업로드 실패", e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	public Resource getProfileImage(User user) {
		if (user.getProfileImage().isEmpty()) {
			log.error("아직 프로필 이미지가 없습니다. 기본 이미지를 제공해드리겠습니다.");
			throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
		}
		String imagePath = user.getProfileImage(); // 예: "profile/201802265/profile.jpg"
		Path path = Paths.get(imagePath);

		try {
			Resource resource = new UrlResource(path.toUri());
			if (!resource.exists()) {
				log.error("이미지를 찾을수없습니다.");
				throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
			}
			return resource;

		} catch (IOException e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	public String getProfileImageContentType(Resource resource) {
		try {
			return Files.probeContentType(resource.getFile().toPath());
		} catch (IOException e) {
			return MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
	}
}
