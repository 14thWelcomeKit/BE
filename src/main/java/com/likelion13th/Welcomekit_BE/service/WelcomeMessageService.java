package com.likelion13th.Welcomekit_BE.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.WelcomeMessage;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.repository.WelcomeMessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WelcomeMessageService {

	@Autowired
	private final WelcomeMessageRepository welcomeMessageRepository;

	public WelcomeMessage getLatestByReceiver(User receiver) {
		return welcomeMessageRepository.findTopByReceiverOrderByCreatedAtDesc(receiver)
			.orElseThrow(() -> new CustomException(ErrorCode.WELCOME_MESSAGE_NOT_FOUND));
	}

	public WelcomeMessage save(WelcomeMessage welcomeMessage) {
		return welcomeMessageRepository.save(welcomeMessage);
	}
}

