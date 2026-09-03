package com.likelion13th.Welcomekit_BE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 MVC 공통 설정.
 * - 모든 REST API 경로에 공통 Base URL(/api/v3/welcome-kit) 접두사를 자동으로 부여한다.
 *   개별 컨트롤러의 @RequestMapping 에는 접두사 이후의 "큰 기능/세부 기능" 경로만 작성한다.
 *   (예: @RequestMapping("/auth") -> 실제 매핑 /api/v3/welcome-kit/auth)
 * - SPA 포워딩용 FrontendController(@Controller)에는 접두사가 붙지 않는다.
 * - 프로필 이미지 정적 리소스 핸들러를 등록한다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	/** 모든 REST API 의 공통 Base URL 접두사 */
	public static final String API_BASE_PATH = "/api/v3/welcome-kit";

	/** 공통 접두사를 적용할 애플리케이션 컨트롤러의 기본 패키지 */
	private static final String APP_BASE_PACKAGE = "com.likelion13th.Welcomekit_BE";

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		// 우리 애플리케이션 패키지의 @RestController 에만 공통 접두사를 적용한다.
		// (SpringDoc 등 외부 라이브러리의 @RestController 에는 적용하지 않아
		//  /v3/api-docs, /swagger 문서 엔드포인트가 정상 동작하도록 한다.)
		configurer.addPathPrefix(API_BASE_PATH,
			handlerType -> handlerType.isAnnotationPresent(RestController.class)
				&& handlerType.getPackageName().startsWith(APP_BASE_PACKAGE));
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/profile/**")
			.addResourceLocations("file:./profile/");
	}
}
