package com.likelion13th.Welcomekit_BE.service;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.WelcomeKitPhoto;
import com.likelion13th.Welcomekit_BE.domain.WelcomeKitPhotoImage;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateWelcomeKitPhotoRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.GenerateUploadUrlRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.request.UpdateWelcomeKitPhotoRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.PhotoDetailResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.PhotoListResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.UpdatePhotoResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.UploadUrlResponse;
import com.likelion13th.Welcomekit_BE.domain.enums.UserType;
import com.likelion13th.Welcomekit_BE.exception.PhotoException;
import com.likelion13th.Welcomekit_BE.repository.WelcomeKitPhotoRepository;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class WelcomeKitPhotoService {

	private static final int DEFAULT_SIZE = 12;
	private static final Duration UPLOAD_URL_EXPIRY = Duration.ofMinutes(5);
	private static final Map<String, String> EXTENSION_BY_CONTENT_TYPE = Map.of(
		"image/jpeg", "jpg",
		"image/png", "png",
		"image/webp", "webp"
	);

	private final WelcomeKitPhotoRepository photoRepository;
	private final UserService userService;
	private final S3Presigner s3Presigner;

	@Value("${aws.s3.bucket}")
	private String bucket;

	@Value("${aws.s3.region}")
	private String region;

	// 1. 사진 게시글 전체 목록 조회
	@Transactional(readOnly = true)
	public PhotoListResponse getPhotoList(int page, int size, String category) {
		int safePage = Math.max(page, 0);
		int safeSize = size < 1 ? DEFAULT_SIZE : size;

		Pageable pageable = PageRequest.of(safePage, safeSize,
			Sort.by(Sort.Direction.DESC, "eventDate").and(Sort.by(Sort.Direction.DESC, "id")));

		Page<WelcomeKitPhoto> result = (category == null || category.isBlank())
			? photoRepository.findAll(pageable)
			: photoRepository.findByCategory(category, pageable);

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
			.category(photo.getCategory())
			.thumbnailUrl(thumbnailUrl)
			.eventDate(photo.getEventDate() != null ? photo.getEventDate().toString() : null)
			.build();
	}

	// 2. 사진 게시글 작성 (운영진 전용)
	@Transactional
	public PhotoListResponse.PhotoSummary createPost(String requesterEmail, CreateWelcomeKitPhotoRequest request) {
		User author = userService.getUserByEmail(requesterEmail);
		if (author.getUserType() != UserType.ADMIN) {
			throw new PhotoException(HttpStatus.FORBIDDEN, "E403", "운영진만 작성할 수 있습니다.");
		}

		WelcomeKitPhoto photo = WelcomeKitPhoto.builder()
			.title(request.getTitle())
			.content(request.getContent())
			.category(request.getCategory())
			.eventDate(request.getEventDate())
			.author(author)
			.build();

		List<String> photoUrls = request.getPhotoUrls();
		for (int i = 0; i < photoUrls.size(); i++) {
			photo.getImages().add(WelcomeKitPhotoImage.builder()
				.imageUrl(photoUrls.get(i))
				.sortOrder(i)
				.photo(photo)
				.build());
		}

		WelcomeKitPhoto saved = photoRepository.save(photo);

		return PhotoListResponse.PhotoSummary.builder()
			.postId(saved.getId())
			.title(saved.getTitle())
			.category(saved.getCategory())
			.thumbnailUrl(photoUrls.get(0))
			.eventDate(saved.getEventDate().toString())
			.build();
	}

	// 3. 사진첩 게시글 상세 조회 (비로그인 허용, requesterEmail 은 null 가능)
	@Transactional(readOnly = true)
	public PhotoDetailResponse getPhotoDetail(Long postId, String requesterEmail) {
		WelcomeKitPhoto photo = photoRepository.findById(postId)
			.orElseThrow(() -> new PhotoException(HttpStatus.NOT_FOUND, "E404", "존재하지 않는 게시글입니다."));

		List<WelcomeKitPhotoImage> sortedImages = photo.getImages().stream()
			.sorted(Comparator.comparingInt(WelcomeKitPhotoImage::getSortOrder))
			.toList();
		List<String> photoUrls = sortedImages.stream().map(WelcomeKitPhotoImage::getImageUrl).toList();
		List<Long> photoIds = sortedImages.stream().map(WelcomeKitPhotoImage::getId).toList();

		User author = photo.getAuthor();
		boolean isOwner = requesterEmail != null && author != null
			&& requesterEmail.equals(author.getEmail());

		return PhotoDetailResponse.builder()
			.postId(photo.getId())
			.title(photo.getTitle())
			.category(photo.getCategory())
			.photoUrls(photoUrls)
			.photoIds(photoIds)
			.content(photo.getContent())
			.eventDate(photo.getEventDate() != null ? photo.getEventDate().toString() : null)
			.authorNickname(author != null ? author.getUserName() : null)
			.isOwner(isOwner)
			.build();
	}

	// 4. 사진첩 게시글 수정 (작성자 본인 전용)
	@Transactional
	public UpdatePhotoResponse updatePost(Long postId, String requesterEmail, UpdateWelcomeKitPhotoRequest request) {
		WelcomeKitPhoto photo = photoRepository.findById(postId)
			.orElseThrow(() -> new PhotoException(HttpStatus.NOT_FOUND, "E404", "존재하지 않는 게시글입니다."));

		User author = photo.getAuthor();
		if (author == null || !requesterEmail.equals(author.getEmail())) {
			throw new PhotoException(HttpStatus.FORBIDDEN, "E403", "작성자만 수정할 수 있습니다.");
		}

		List<Long> deleteIds = request.getDeletePhotoIds();
		List<String> addUrls = request.getAddPhotoUrls();

		long deleteCount = deleteIds == null ? 0
			: photo.getImages().stream().filter(image -> deleteIds.contains(image.getId())).count();
		int addCount = addUrls == null ? 0 : addUrls.size();
		long prospectiveCount = photo.getImages().size() - deleteCount + addCount;

		if (prospectiveCount > 5) {
			throw new PhotoException(HttpStatus.BAD_REQUEST, "E400_PHOTO", "사진은 최대 5장까지 업로드할 수 있습니다.");
		}
		if (prospectiveCount < 1) {
			throw new PhotoException(HttpStatus.BAD_REQUEST, "E401_PHOTO_MIN", "사진은 최소 1장 이상 유지해야 합니다.");
		}

		if (deleteIds != null && !deleteIds.isEmpty()) {
			// orphanRemoval=true 라 컬렉션에서 빼면 save 시 실제 행도 삭제된다.
			photo.getImages().removeIf(image -> deleteIds.contains(image.getId()));
		}

		if (addUrls != null && !addUrls.isEmpty()) {
			int nextSortOrder = photo.getImages().stream()
				.mapToInt(WelcomeKitPhotoImage::getSortOrder)
				.max()
				.orElse(-1) + 1;
			for (String url : addUrls) {
				photo.getImages().add(WelcomeKitPhotoImage.builder()
					.imageUrl(url)
					.sortOrder(nextSortOrder++)
					.photo(photo)
					.build());
			}
		}

		if (request.getTitle() != null) {
			photo.setTitle(request.getTitle());
		}
		if (request.getCategory() != null) {
			photo.setCategory(request.getCategory());
		}
		if (request.getEventDate() != null) {
			photo.setEventDate(request.getEventDate());
		}
		if (request.getContent() != null) {
			photo.setContent(request.getContent());
		}

		WelcomeKitPhoto saved = photoRepository.save(photo);

		// 대표 사진(첫 번째, sortOrder 최소)이 지워졌다면 남은 사진 중 최소값이 자연히 새 썸네일이 된다.
		List<String> photoUrls = saved.getImages().stream()
			.sorted(Comparator.comparingInt(WelcomeKitPhotoImage::getSortOrder))
			.map(WelcomeKitPhotoImage::getImageUrl)
			.toList();

		return UpdatePhotoResponse.builder()
			.postId(saved.getId())
			.title(saved.getTitle())
			.category(saved.getCategory())
			.eventDate(saved.getEventDate() != null ? saved.getEventDate().toString() : null)
			.thumbnailUrl(photoUrls.isEmpty() ? null : photoUrls.get(0))
			.photoUrls(photoUrls)
			.build();
	}

	// 5. 이미지 업로드용 presigned URL 발급 (운영진 전용)
	@Transactional(readOnly = true)
	public UploadUrlResponse generateUploadUrls(String requesterEmail, GenerateUploadUrlRequest request) {
		User requester = userService.getUserByEmail(requesterEmail);
		if (requester.getUserType() != UserType.ADMIN) {
			throw new PhotoException(HttpStatus.FORBIDDEN, "E403", "운영진만 업로드할 수 있습니다.");
		}

		List<UploadUrlResponse.UrlPair> urls = request.getFiles().stream()
			.map(file -> generateUploadUrl(file.getContentType()))
			.toList();

		return UploadUrlResponse.builder().urls(urls).build();
	}

	private UploadUrlResponse.UrlPair generateUploadUrl(String contentType) {
		String extension = EXTENSION_BY_CONTENT_TYPE.get(contentType);
		String key = "photos/" + UUID.randomUUID() + "." + extension;

		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType(contentType)
			.build();

		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(UPLOAD_URL_EXPIRY)
			.putObjectRequest(putObjectRequest)
			.build();

		PresignedPutObjectRequest presigned = s3Presigner.presignPutObject(presignRequest);
		String fileUrl = "https://%s.s3.%s.amazonaws.com/%s".formatted(bucket, region, key);

		return UploadUrlResponse.UrlPair.builder()
			.uploadUrl(presigned.url().toString())
			.fileUrl(fileUrl)
			.build();
	}
}
