package com.likelion13th.Welcomekit_BE.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13th.Welcomekit_BE.domain.EmailVerification;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.repository.EmailVerificationRepository;
import com.likelion13th.Welcomekit_BE.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 학교 이메일 인증코드 발송/검증 서비스.
 * - 허용 도메인(app.signup.email-domain)으로만 인증코드 발송
 * - 6자리 코드, 만료시간(app.signup.code-ttl-minutes) 경과 시 만료
 * - 인증 성공 시 verified=true 로 표시, 회원가입 시 이 값을 확인
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

	private static final SecureRandom RANDOM = new SecureRandom();

	private final EmailVerificationRepository emailVerificationRepository;
	private final UserRepository userRepository;
	private final JavaMailSender mailSender;

	@Value("${spring.mail.username:}")
	private String fromAddress;

	@Value("${app.signup.email-domain:}")
	private String allowedEmailDomain;

	@Value("${app.signup.code-ttl-minutes:10}")
	private long codeTtlMinutes;

	@Transactional
	public void sendCode(String email) {
		validateDomain(email);

		if (userRepository.existsByEmail(email)) {
			throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}

		String code = generateCode();
		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(codeTtlMinutes);

		// 이메일당 최신 코드 1건만 유지(있으면 갱신, 없으면 생성)
		EmailVerification verification = emailVerificationRepository.findByEmail(email)
			.map(existing -> {
				existing.setCode(code);
				existing.setExpiresAt(expiresAt);
				existing.setVerified(false);
				return existing;
			})
			.orElseGet(() -> EmailVerification.builder()
				.email(email)
				.code(code)
				.expiresAt(expiresAt)
				.verified(false)
				.build());

		emailVerificationRepository.save(verification);

		sendMail(email, code);
	}

	/**
	 * 비밀번호 재설정(찾기)용 인증코드 발송.
	 * 회원가입용 sendCode()와 달리, 이미 가입된 이메일에만 발송한다(가입돼 있지 않으면 에러).
	 */
	@Transactional
	public void sendCodeForReset(String email) {
		validateDomain(email);

		if (!userRepository.existsByEmail(email)) {
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
		}

		String code = generateCode();
		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(codeTtlMinutes);

		// 이메일당 최신 코드 1건만 유지(있으면 갱신, 없으면 생성)
		EmailVerification verification = emailVerificationRepository.findByEmail(email)
			.map(existing -> {
				existing.setCode(code);
				existing.setExpiresAt(expiresAt);
				existing.setVerified(false);
				return existing;
			})
			.orElseGet(() -> EmailVerification.builder()
				.email(email)
				.code(code)
				.expiresAt(expiresAt)
				.verified(false)
				.build());

		emailVerificationRepository.save(verification);

		sendMail(email, code);
	}

	@Transactional
	public void verifyCode(String email, String code) {
		EmailVerification verification = emailVerificationRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.VERIFICATION_CODE_NOT_FOUND));

		if (verification.isExpired()) {
			throw new CustomException(ErrorCode.VERIFICATION_CODE_EXPIRED);
		}
		if (!verification.getCode().equals(code)) {
			throw new CustomException(ErrorCode.VERIFICATION_CODE_MISMATCH);
		}

		verification.setVerified(true);
		emailVerificationRepository.save(verification);
	}

	/**
	 * 회원가입 시 호출: 해당 이메일이 인증 완료(verified) 상태이며 만료되지 않았는지 확인.
	 */
	@Transactional(readOnly = true)
	public void assertVerified(String email) {
		EmailVerification verification = emailVerificationRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.EMAIL_NOT_VERIFIED));

		if (!verification.isVerified() || verification.isExpired()) {
			throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
		}
	}

	private void validateDomain(String email) {
		// 도메인 미설정 시 도메인 제한 없이 통과(운영에서는 반드시 설정 권장)
		if (allowedEmailDomain == null || allowedEmailDomain.isBlank()) {
			return;
		}
		String normalized = email.toLowerCase();
		String domain = allowedEmailDomain.toLowerCase();
		if (!normalized.endsWith("@" + domain)) {
			throw new CustomException(ErrorCode.INVALID_EMAIL_DOMAIN);
		}
	}

	private String generateCode() {
		int number = RANDOM.nextInt(1_000_000); // 0 ~ 999999
		return String.format("%06d", number);
	}

	private void sendMail(String email, String code) {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			if (fromAddress != null && !fromAddress.isBlank()) {
				message.setFrom(fromAddress);
			}
			message.setTo(email);
			message.setSubject("[웰컴키트] 이메일 인증코드");
			message.setText(
				"안녕하세요, 웰컴키트입니다.\n\n"
					+ "아래 인증코드를 회원가입 화면에 입력해주세요.\n\n"
					+ "인증코드: " + code + "\n\n"
					+ "인증코드는 " + codeTtlMinutes + "분 후 만료됩니다."
			);
			mailSender.send(message);
			log.info("인증 메일 발송 완료: {}", email);
		} catch (Exception e) {
			log.error("인증 메일 발송 실패: {}", email, e);
			throw new CustomException(ErrorCode.EMAIL_SEND_FAILED);
		}
	}
}
