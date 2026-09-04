package com.likelion13th.Welcomekit_BE.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * 사진첩 이미지 업로드용 S3 presigned URL 발급에 쓰이는 {@link S3Presigner} 빈 설정.
 * 실제 파일 바이트는 서버를 거치지 않고 클라이언트가 S3 에 직접 PUT 한다.
 */
@Configuration
public class S3Config {

	@Value("${aws.access-key}")
	private String accessKey;

	@Value("${aws.secret-key}")
	private String secretKey;

	@Value("${aws.s3.region}")
	private String region;

	@Bean
	public S3Presigner s3Presigner() {
		AwsCredentialsProvider credentialsProvider =
			StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));

		return S3Presigner.builder()
			.region(Region.of(region))
			.credentialsProvider(credentialsProvider)
			.build();
	}
}
