package com.likelion13th.Welcomekit_BE.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.likelion13th.Welcomekit_BE.jwt.JwtAuthenticationEntryPoint;
import com.likelion13th.Welcomekit_BE.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationEntryPoint unauthorizedHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationEntryPoint unauthorizedHandler,
		JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.unauthorizedHandler = unauthorizedHandler;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.sessionManagement(
				sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(unauthorizedHandler))
			.authorizeHttpRequests(authorizeRequests -> authorizeRequests
				// 정적 리소스 / 진입점
				.requestMatchers("/", "/index.html", "/favicon.ico", "/manifest.json", "/robots.txt",
					"/logo192.png", "/logo512.png", "/asset-manifest.json", "/static/**", "/sw.js").permitAll()
				// 인증 없이 허용되는 API: 로그인 / 회원가입 / 이메일 인증
				.requestMatchers(HttpMethod.POST, "/api/v3/welcome-kit/auth/sign-in").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v3/welcome-kit/auth/email/send-code").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v3/welcome-kit/auth/email/verify-code").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v3/welcome-kit/auth/reset-password/send-code").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v3/welcome-kit/auth/reset-password").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v3/welcome-kit/user/join").permitAll()
				// 사진첩 목록/상세 조회는 비로그인 허용 (상세의 isOwner 는 토큰 있을 때만 계산)
				.requestMatchers(HttpMethod.GET, "/api/v3/welcome-kit/photos").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v3/welcome-kit/photos/*").permitAll()
				// Swagger 문서
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger",
					"/swagger-resources/**", "/webjars/**").permitAll()
				// 그 외 모든 요청은 인증 필요
				.anyRequest().authenticated()
			);

		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("*")); // 모든 도메인 허용 (Credentials와 함께 사용 가능)
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}