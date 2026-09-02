package com.likelion13th.Welcomekit_BE.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.WelcomeMessage;
import com.likelion13th.Welcomekit_BE.domain.dto.response.GetWelcomeMessageResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.UpdateWelcomeReadResponse;
import com.likelion13th.Welcomekit_BE.service.UserService;
import com.likelion13th.Welcomekit_BE.service.WelcomeMessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WelcomeMessageManager {

	@Autowired
	private final WelcomeMessageService welcomeMessageService;
	@Autowired
	private final UserService userService;

	public GetWelcomeMessageResponse getMyWelcomeMessage(String email) {
		User receiver = userService.getUserByEmail(email);
		WelcomeMessage welcomeMessage = welcomeMessageService.getLatestByReceiver(receiver);

		return GetWelcomeMessageResponse.builder()
			.id(welcomeMessage.getId())
			.receiverName(welcomeMessage.getReceiver().getUserName())
			.senderName(welcomeMessage.getSender().getUserName())
			.content(welcomeMessage.getContent())
			.createdAt(welcomeMessage.getCreatedAt())
			.build();
	}

	public UpdateWelcomeReadResponse updateHasReadWelcome(String email) {
		User user = userService.getUserByEmail(email);
		userService.updateHasReadWelcome(user, true);

		return UpdateWelcomeReadResponse.builder()
			.message("웰컴 메시지 읽음 상태가 업데이트되었습니다.")
			.hasReadWelcome(true)
			.build();
	}
}

