package com.likelion13th.Welcomekit_BE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 14기 빙고 V1의 12시간 타이머 만료 처리를 위한 스케줄러 설정.
 * 기존 코드와 독립적인 새 설정 파일로 추가.
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
