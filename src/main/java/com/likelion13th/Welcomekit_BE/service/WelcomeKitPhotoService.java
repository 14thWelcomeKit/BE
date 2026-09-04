package com.likelion13th.Welcomekit_BE.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13th.Welcomekit_BE.domain.WelcomeKitPhoto;
import com.likelion13th.Welcomekit_BE.domain.WelcomeKitPhotoImage;
import com.likelion13th.Welcomekit_BE.domain.dto.response.PhotoListResponse;
import com.likelion13th.Welcomekit_BE.repository.WelcomeKitPhotoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WelcomeKitPhotoService {

	private static final int DEFAULT_SIZE = 12;

	private final WelcomeKitPhotoRepository photoRepository;

	/**
	 * 사진첩 게시글을 게시일(createdAt) 내림차순으로 페이지네이션 조회한다.
	 * page 는 0-based, 잘못된 값은 안전한 기본값으로 보정한다.
	 */
	@Transactional(readOnly = true)
	public PhotoListResponse getPhotoList(int page, int size) {
		int safePage = Math.max(page, 0);
		int safeSize = size < 1 ? DEFAULT_SIZE : size;

		Pageable pageable = PageRequest.of(safePage, safeSize,
			Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id")));

		Page<WelcomeKitPhoto> result = photoRepository.findAll(pageable);

		List<PhotoListResponse.PhotoSummary> posts = result.getContent().stream()
			.map(this::toSummary)
			.toList();

		return PhotoListResponse.builder()
			.posts(posts)
			.pageInfo(PhotoListResponse.PageInfo.builder()
				.page(safePage)
				.size(safeSize)
				.totalElements(result.getTotalElements())
				.totalPages(result.getTotalPages())
				.build())
			.build();
	}

	private PhotoListResponse.PhotoSummary toSummary(WelcomeKitPhoto photo) {
		String thumbnailUrl = photo.getImages().stream()
			.min(Comparator.comparingInt(WelcomeKitPhotoImage::getSortOrder))
			.map(WelcomeKitPhotoImage::getImageUrl)
			.orElse(null);

		return PhotoListResponse.PhotoSummary.builder()
			.postId(photo.getId())
			.title(photo.getTitle())
			.thumbnailUrl(thumbnailUrl)
			.postedAt(photo.getCreatedAt() != null ? photo.getCreatedAt().toLocalDate().toString() : null)
			.build();
	}
}
