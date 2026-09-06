package com.likelion13th.Welcomekit_BE;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class WelcomekitBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(WelcomekitBeApplication.class, args);
	}

	/**
	 * 애플리케이션 기본 타임존을 한국 시간(KST)으로 고정한다.
	 * 배포 서버(컨테이너)가 UTC 기준이라 LocalDateTime.now()가 9시간 이르게 찍히던 문제 방지.
	 */
	@PostConstruct
	public void setTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
