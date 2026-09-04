package com.likelion13th.Welcomekit_BE.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.likelion13th.Welcomekit_BE.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

@Component
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration}")
	private long jwtExpirationInMs;

	private SecretKey key;

	@PostConstruct
	void init() {
		// jwt.secret 을 원문(UTF-8) 바이트 그대로 서명 키로 사용한다.
		// HS512 는 최소 512bit(64바이트) 키가 필요하므로 jwt.secret 은 64자 이상이어야 한다.
		// (기존 deprecated 오버로드 signWith(alg, String) 은 secret 을 Base64 로 디코드해
		//  실제 키 길이가 줄어들어 WeakKeyException 이 발생했다.)
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder()
			.setSubject(user.getEmail())
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(key, SignatureAlgorithm.HS512)
			.compact();
	}

	public String getUserIdFromJWT(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();

		return claims.getSubject();
	}

	public boolean validateToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
}
