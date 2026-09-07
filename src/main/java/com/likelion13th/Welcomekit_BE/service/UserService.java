package com.likelion13th.Welcomekit_BE.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateUserRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetAllBabyLionResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetMyInfoResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.ProfileImageUploadUrlResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private static final Duration PROFILE_UPLOAD_URL_EXPIRY = Duration.ofMinutes(5);
	private static final Map<String, String> PROFILE_EXTENSION_BY_CONTENT_TYPE = Map.of(
		"image/jpeg", "jpg",
		"image/png", "png",
		"image/webp", "webp"
	);

	@Autowired
	private final UserRepository userRepository;

	@Autowired
	private final PasswordEncoder passwordEncoder;

	@Autowired
	private final EmailVerificationService emailVerificationService;

	@Autowired
	private final S3Presigner s3Presigner;

	@Autowired
	private final S3Client s3Client;

	@Value("${app.signup.admin-invite-code:}")
	private String adminInviteCode;

	@Value("${aws.s3.bucket}")
	private String bucket;

	@Value("${aws.s3.region}")
	private String region;

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
		getMyInfoResponse.setUserType(user.getUserType());
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

	/** 프로필 이미지 업로드용 presigned URL 발급. 파일 바이트는 서버를 거치지 않고 클라이언트가 S3 에 직접 PUT한다. */
	public ProfileImageUploadUrlResponse generateProfileImageUploadUrl(String contentType) {
		String extension = PROFILE_EXTENSION_BY_CONTENT_TYPE.get(contentType);
		String key = "profile/" + UUID.randomUUID() + "." + extension;

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType(contentType)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(PROFILE_UPLOAD_URL_EXPIRY)
			.putObjectRequest(putObjectRequest)
			.build();

		PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
		String fileUrl = "https://%s.s3.%s.amazonaws.com/%s".formatted(bucket, region, key);

		return new ProfileImageUploadUrlResponse(presigned.url().toString(), fileUrl);
	}

	/** S3 업로드 완료 후, 발급받은 fileUrl 을 실제 프로필 이미지로 반영한다. 기존 S3 이미지가 있었다면 정리한다. */
	public void updateProfileImage(User user, String fileUrl) {
		String previousImage = user.getProfileImage();

		user.setProfileImage(fileUrl);
		userRepository.save(user);

		String previousKey = extractS3KeyOrNull(previousImage);
		if (previousKey != null) {
			try {
				s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(previousKey).build());
			} catch (Exception e) {
				log.warn("[프로필 이미지] 기존 S3 파일 삭제 실패 (교체는 계속 진행): key={}", previousKey, e);
			}
		}
	}

	private String extractS3KeyOrNull(String imageUrl) {
		String prefix = "https://%s.s3.%s.amazonaws.com/".formatted(bucket, region);
		return (imageUrl != null && imageUrl.startsWith(prefix)) ? imageUrl.substring(prefix.length()) : null;
	}
}
