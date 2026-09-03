package com.likelion13th.Welcomekit_BE.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		Info info = new Info()
			.title("WelcomeKit API")
			.version("v3")
			.description("멋쟁이사자처럼 WelcomeKit 백엔드 API 문서입니다. "
				+ "모든 REST API 의 공통 Base URL 은 /api/v3/welcome-kit 이며, "
				+ "각 엔드포인트는 [공통 Base URL]/[큰 기능]/[세부 기능] 형식을 따릅니다. "
				+ "대부분의 API 는 JWT(Bearer) 인증이 필요합니다.")
			.contact(new Contact()
				.name("HyunWoo9930")
				.email("hw62459930@gmail.com"));

		return new OpenAPI()
			.info(info)
			.servers(List.of(
				new Server().url("https://welcomekit14.hufsglobalikelion.co.kr").description("배포 서버")
			))
			.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
			.components(new io.swagger.v3.oas.models.Components()
				.addSecuritySchemes("Bearer Authentication", new SecurityScheme()
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT")));
	}
}
