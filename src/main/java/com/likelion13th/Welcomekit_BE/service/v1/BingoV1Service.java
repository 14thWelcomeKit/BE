package com.likelion13th.Welcomekit_BE.service.v1;

import com.likelion13th.Welcomekit_BE.domain.Team;
import com.likelion13th.Welcomekit_BE.domain.User;
import com.likelion13th.Welcomekit_BE.domain.dto.v1.response.*;
import com.likelion13th.Welcomekit_BE.domain.enums.v1.ActivityLogStatusV1;
import com.likelion13th.Welcomekit_BE.domain.enums.v1.BingoCellStatusV1;
import com.likelion13th.Welcomekit_BE.domain.v1.BingoActivityLogV1;
import com.likelion13th.Welcomekit_BE.domain.v1.BingoCellV1;
import com.likelion13th.Welcomekit_BE.exception.CustomException;
import com.likelion13th.Welcomekit_BE.exception.ErrorCode;
import com.likelion13th.Welcomekit_BE.repository.v1.BingoActivityLogV1Repository;
import com.likelion13th.Welcomekit_BE.repository.v1.BingoCellV1Repository;
import com.likelion13th.Welcomekit_BE.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BingoV1Service {

	private static final int OCCUPY_HOURS = 12;
	private static final String IMAGE_BASE_DIR = "/app/external-bingo-v1/";

	private final BingoCellV1Repository bingoCellV1Repository;
	private final BingoActivityLogV1Repository activityLogV1Repository;
	private final UserService userService;

	// ══════════════════════════════════════════════════════════
	// 1. 글로벌 빙고판 조회  GET /api/v1/bingo/global
	// ══════════════════════════════════════════════════════════

	@Transactional(readOnly = true)
	public GlobalBingoBoardResponse getGlobalBoard() {
		List<GlobalBingoBoardResponse.CellSummary> summaries = bingoCellV1Repository.findAllByOrderByIdAsc()
			.stream()
			.map(cell -> {
				BingoCellStatusV1 effective = effectiveStatus(cell);
				return GlobalBingoBoardResponse.CellSummary.builder()
					.cellId(cell.getId())
					.mission(cell.getMission())
					.status(effective)
					.imageUrl(effective != BingoCellStatusV1.EMPTY ? imageUrl(cell.getId()) : null)
					.teamName(cell.getOwnerTeam() != null ? cell.getOwnerTeam().getTeamName() : null)
					.build();
			})
			.collect(Collectors.toList());

		return GlobalBingoBoardResponse.builder().cells(summaries).build();
	}

	// ══════════════════════════════════════════════════════════
	// 2. 글로벌 칸 상세 조회  GET /api/v1/bingo/global/{cellId}
	// ══════════════════════════════════════════════════════════

	@Transactional(readOnly = true)
	public GlobalBingoCellDetailResponse getGlobalCellDetail(Long cellId) {
		BingoCellV1 cell = findCell(cellId);
		BingoCellStatusV1 effective = effectiveStatus(cell);

		String remainingTime = null;
		if (effective == BingoCellStatusV1.PROCESSING) {
			remainingTime = formatRemainingTime(cell.getOccupiedAt().plusHours(OCCUPY_HOURS));
		}

		String statusLabel = switch (effective) {
			case EMPTY -> "도전 가능";
			case PROCESSING -> "진행 중";
			case OCCUPIED -> "점유 완료";
		};

		return GlobalBingoCellDetailResponse.builder()
			.cellId(cell.getId())
			.status(effective)
			.mission(GlobalBingoCellDetailResponse.MissionInfo.builder()
				.title(cell.getMission())
				.description(null)
				.build())
			.displayData(GlobalBingoCellDetailResponse.DisplayData.builder()
				.imageUrl(effective != BingoCellStatusV1.EMPTY ? imageUrl(cell.getId()) : null)
				.teamName(cell.getOwnerTeam() != null ? cell.getOwnerTeam().getTeamName() : null)
				.remainingTime(remainingTime)
				.statusLabel(statusLabel)
				.build())
			.build();
	}

	// ══════════════════════════════════════════════════════════
	// 3. 팀 빙고판 조회  GET /api/v1/bingo/team
	// ══════════════════════════════════════════════════════════

	@Transactional(readOnly = true)
	public TeamBingoBoardResponse getTeamBoard(String studentNum) {
		Team myTeam = requireTeam(userService.getUserByStudentNum(studentNum));

		List<TeamBingoBoardResponse.TeamCellSummary> summaries = bingoCellV1Repository.findAllByOrderByIdAsc()
			.stream()
			.map(cell -> {
				BingoCellStatusV1 effective = effectiveStatus(cell);
				boolean isOurs = cell.getOwnerTeam() != null
					&& cell.getOwnerTeam().getId().equals(myTeam.getId());

				String borderType = switch (effective) {
					case EMPTY -> "NONE";
					case PROCESSING -> isOurs ? "PROCESSING_OURS" : "PROCESSING_OTHERS";
					case OCCUPIED -> isOurs ? "BLACK" : "GRAY";
				};

				return TeamBingoBoardResponse.TeamCellSummary.builder()
					.cellId(cell.getId())
					.mission(cell.getMission())
					.status(effective)
					.isOurTeam(isOurs)
					.borderType(borderType)
					.imageUrl(effective != BingoCellStatusV1.EMPTY ? imageUrl(cell.getId()) : null)
					.build();
			})
			.collect(Collectors.toList());

		return TeamBingoBoardResponse.builder()
			.teamName(myTeam.getTeamName())
			.cells(summaries)
			.build();
	}

	// ══════════════════════════════════════════════════════════
	// 4. 팀 미션 상세 조회  GET /api/v1/bingo/team/{cellId}
	// ══════════════════════════════════════════════════════════

	@Transactional(readOnly = true)
	public TeamBingoCellDetailResponse getTeamCellDetail(String studentNum, Long cellId) {
		requireTeam(userService.getUserByStudentNum(studentNum)); // 팀 소속 여부 검증
		BingoCellV1 cell = findCell(cellId);
		BingoCellStatusV1 effective = effectiveStatus(cell);

		Long remainingSeconds = null;
		if (effective == BingoCellStatusV1.PROCESSING) {
			LocalDateTime deadline = cell.getOccupiedAt().plusHours(OCCUPY_HOURS);
			remainingSeconds = Math.max(0, Duration.between(LocalDateTime.now(), deadline).toSeconds());
		}

		return TeamBingoCellDetailResponse.builder()
			.cellId(cell.getId())
			.status(effective)
			.mission(TeamBingoCellDetailResponse.MissionInfo.builder()
				.title(cell.getMission())
				.description(null)
				.build())
			.displayData(TeamBingoCellDetailResponse.DisplayData.builder()
				.teamName(cell.getOwnerTeam() != null ? cell.getOwnerTeam().getTeamName() : null)
				.imageUrl(effective != BingoCellStatusV1.EMPTY ? imageUrl(cell.getId()) : null)
				.remainingSeconds(remainingSeconds)
				.isConfirmed(effective == BingoCellStatusV1.OCCUPIED)
				.build())
			.updatedAt(cell.getOccupiedAt())
			.build();
	}

	// ══════════════════════════════════════════════════════════
	// 5. 미션 사진 업로드  POST /api/v1/bingo/team/{cellId}/upload
	// ══════════════════════════════════════════════════════════

	@Transactional
	public UploadMissionResponse uploadMission(String studentNum, Long cellId, MultipartFile file) {
		Team myTeam = requireTeam(userService.getUserByStudentNum(studentNum));

		// 비관적 쓰기 잠금으로 동시 업로드를 직렬화
		BingoCellV1 cell = bingoCellV1Repository.findByIdWithPessimisticLock(cellId)
			.orElseThrow(() -> new CustomException(ErrorCode.CELL_NOT_FOUND));

		BingoCellStatusV1 effective = effectiveStatus(cell);

		if (effective == BingoCellStatusV1.OCCUPIED) {
			throw new CustomException(ErrorCode.CELL_ALREADY_OCCUPIED);
		}

		if (effective == BingoCellStatusV1.PROCESSING) {
			if (cell.getOwnerTeam().getId().equals(myTeam.getId())) {
				// 본인 팀은 PATCH로 수정해야 함
				throw new CustomException(ErrorCode.USE_PATCH_FOR_UPDATE);
			}
			// ── 다른 팀이 가로채기 ──
			// 1) 기존 이미지 삭제
			deleteImageFile(cell.getImagePath());
			// 2) 기존 팀에 VOID 로그
			saveActivityLog(
				cell.getOwnerTeam(), cell, ActivityLogStatusV1.VOID,
				cell.getId() + "번 칸 독점이 다른 팀의 업로드로 무효화되었습니다."
			);
			log.info("[BingoV1] Cell {} intercepted: {} → {}",
				cellId, cell.getOwnerTeam().getTeamName(), myTeam.getTeamName());
		}

		String imagePath = saveImageFile(file, cellId);
		LocalDateTime now = LocalDateTime.now();

		cell.setStatus(BingoCellStatusV1.PROCESSING);
		cell.setOwnerTeam(myTeam);
		cell.setImagePath(imagePath);
		cell.setOccupiedAt(now);
		bingoCellV1Repository.save(cell);

		return UploadMissionResponse.builder()
			.message("성공적으로 업로드되었습니다. 12시간의 독점이 시작됩니다.")
			.cellId(cellId)
			.startTime(now)
			.endTime(now.plusHours(OCCUPY_HOURS))
			.build();
	}

	// ══════════════════════════════════════════════════════════
	// 6. 업로드 사진 수정  PATCH /api/v1/bingo/team/{cellId}/upload
	// ══════════════════════════════════════════════════════════

	@Transactional
	public UploadMissionResponse updateMission(String studentNum, Long cellId, MultipartFile file) {
		Team myTeam = requireTeam(userService.getUserByStudentNum(studentNum));

		BingoCellV1 cell = bingoCellV1Repository.findByIdWithPessimisticLock(cellId)
			.orElseThrow(() -> new CustomException(ErrorCode.CELL_NOT_FOUND));

		// 본인 팀 소유 + PROCESSING 상태인지 확인
		if (cell.getStatus() != BingoCellStatusV1.PROCESSING
			|| cell.getOwnerTeam() == null
			|| !cell.getOwnerTeam().getId().equals(myTeam.getId())) {
			throw new CustomException(ErrorCode.NOT_CELL_OWNER);
		}

		// 타이머가 이미 만료되었는지 확인 (실질적으로 OCCUPIED 상태)
		if (effectiveStatus(cell) == BingoCellStatusV1.OCCUPIED) {
			throw new CustomException(ErrorCode.UPDATE_WINDOW_EXPIRED);
		}

		deleteImageFile(cell.getImagePath());
		cell.setImagePath(saveImageFile(file, cellId));
		bingoCellV1Repository.save(cell);

		return UploadMissionResponse.builder()
			.message("사진이 성공적으로 수정되었습니다. 타이머는 유지됩니다.")
			.cellId(cellId)
			.startTime(cell.getOccupiedAt())
			.endTime(cell.getOccupiedAt().plusHours(OCCUPY_HOURS))
			.build();
	}

	// ══════════════════════════════════════════════════════════
	// 7. 내 팀 정보 조회  GET /api/v1/team/me
	// ══════════════════════════════════════════════════════════

	@Transactional(readOnly = true)
	public TeamInfoV1Response getTeamInfo(String studentNum) {
		User user = userService.getUserByStudentNum(studentNum);
		Team myTeam = requireTeam(user);

		long occupiedCount = bingoCellV1Repository.countByOwnerTeamAndStatus(myTeam, BingoCellStatusV1.OCCUPIED);

		// 순위 계산: OCCUPIED 셀 수 기준, 나보다 많은 팀 수 + 1
		Map<Long, Long> teamOccupiedMap = bingoCellV1Repository
			.findByStatus(BingoCellStatusV1.OCCUPIED)
			.stream()
			.filter(c -> c.getOwnerTeam() != null)
			.collect(Collectors.groupingBy(c -> c.getOwnerTeam().getId(), Collectors.counting()));

		long myCount = teamOccupiedMap.getOrDefault(myTeam.getId(), 0L);
		int ranking = (int) teamOccupiedMap.values().stream().filter(v -> v > myCount).count() + 1;

		List<String> memberNames = myTeam.getMembers() != null
			? myTeam.getMembers().stream().map(User::getUserName).collect(Collectors.toList())
			: List.of();

		return TeamInfoV1Response.builder()
			.teamId(myTeam.getId())
			.teamName(myTeam.getTeamName())
			.members(memberNames)
			.progress(TeamInfoV1Response.Progress.builder()
				.occupiedCount(occupiedCount)
				.totalCells(25)
				.ranking(ranking)
				.build())
			.build();
	}

	// ══════════════════════════════════════════════════════════
	// 8. 팀 활동 로그 조회  GET /api/v1/team/activities
	// ══════════════════════════════════════════════════════════

	@Transactional(readOnly = true)
	public TeamActivitiesResponse getTeamActivities(String studentNum) {
		Team myTeam = requireTeam(userService.getUserByStudentNum(studentNum));

		List<TeamActivitiesResponse.ActivityEntry> entries = activityLogV1Repository
			.findByTeamOrderByTimestampDesc(myTeam)
			.stream()
			.map(logEntry -> TeamActivitiesResponse.ActivityEntry.builder()
				.cellId(logEntry.getCell().getId())
				.status(logEntry.getStatus())
				.message(logEntry.getMessage())
				.timestamp(logEntry.getTimestamp())
				.build())
			.collect(Collectors.toList());

		return TeamActivitiesResponse.builder().activities(entries).build();
	}

	// ══════════════════════════════════════════════════════════
	// 9. 이미지 서빙  GET /api/v1/bingo/image/{cellId}
	// ══════════════════════════════════════════════════════════

	@Transactional(readOnly = true)
	public byte[] getCellImage(Long cellId) throws IOException {
		BingoCellV1 cell = findCell(cellId);
		if (cell.getImagePath() == null || cell.getImagePath().isBlank()) {
			throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
		}
		Path path = Paths.get(cell.getImagePath());
		if (!Files.exists(path)) {
			throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
		}
		return Files.readAllBytes(path);
	}

	// ══════════════════════════════════════════════════════════
	// 스케줄러: 12시간 만료된 PROCESSING 셀 → OCCUPIED 전환
	// ══════════════════════════════════════════════════════════

	/**
	 * 매 1분마다 실행.
	 * PROCESSING 셀 중 타이머가 만료된 것을 OCCUPIED로 확정하고 SUCCESS 로그를 생성한다.
	 *
	 * 동시성 주의: 스케줄러도 만료 판정 전에 비관적 잠금을 획득한다.
	 * 업로드 트랜잭션이 진행 중이라면 블로킹되어 잘못된 만료 처리를 방지한다.
	 */
	@Scheduled(fixedDelay = 60_000)
	@Transactional
	public void processExpiredCells() {
		LocalDateTime now = LocalDateTime.now();
		List<BingoCellV1> candidates = bingoCellV1Repository.findByStatus(BingoCellStatusV1.PROCESSING);

		for (BingoCellV1 candidate : candidates) {
			if (candidate.getOccupiedAt() == null
				|| !candidate.getOccupiedAt().plusHours(OCCUPY_HOURS).isBefore(now)) {
				continue;
			}

			// 잠금 재획득 후 상태 재확인 (업로드 직후 스케줄러가 잘못 처리하는 케이스 방지)
			bingoCellV1Repository.findByIdWithPessimisticLock(candidate.getId())
				.ifPresent(locked -> {
					if (locked.getStatus() == BingoCellStatusV1.PROCESSING
						&& locked.getOccupiedAt() != null
						&& locked.getOccupiedAt().plusHours(OCCUPY_HOURS).isBefore(now)) {

						locked.setStatus(BingoCellStatusV1.OCCUPIED);
						bingoCellV1Repository.save(locked);
						saveActivityLog(
							locked.getOwnerTeam(), locked, ActivityLogStatusV1.SUCCESS,
							locked.getId() + "번 칸 독점에 성공하여 영토를 획득했습니다!"
						);
						log.info("[BingoV1] Cell {} confirmed: owned by {}",
							locked.getId(), locked.getOwnerTeam().getTeamName());
					}
				});
		}
	}

	// ══════════════════════════════════════════════════════════
	// 내부 유틸
	// ══════════════════════════════════════════════════════════

	/**
	 * DB 갱신 없이 현재 시각 기준 실질 상태를 계산한다.
	 * (조회 응답에서 타이머 만료 셀을 OCCUPIED 로 보여줄 때 사용)
	 */
	private BingoCellStatusV1 effectiveStatus(BingoCellV1 cell) {
		if (cell.getStatus() == BingoCellStatusV1.PROCESSING
			&& cell.getOccupiedAt() != null
			&& cell.getOccupiedAt().plusHours(OCCUPY_HOURS).isBefore(LocalDateTime.now())) {
			return BingoCellStatusV1.OCCUPIED;
		}
		return cell.getStatus();
	}

	private BingoCellV1 findCell(Long cellId) {
		return bingoCellV1Repository.findById(cellId)
			.orElseThrow(() -> new CustomException(ErrorCode.CELL_NOT_FOUND));
	}

	private Team requireTeam(User user) {
		if (user.getTeam() == null) {
			throw new CustomException(ErrorCode.TEAM_NOT_ASSIGNED);
		}
		return user.getTeam();
	}

	private String imageUrl(Long cellId) {
		return "/api/v1/bingo/image/" + cellId;
	}

	private String formatRemainingTime(LocalDateTime deadline) {
		Duration d = Duration.between(LocalDateTime.now(), deadline);
		if (d.isNegative()) return "00:00:00";
		return String.format("%02d:%02d:%02d", d.toHours(), d.toMinutesPart(), d.toSecondsPart());
	}

	private String saveImageFile(MultipartFile file, Long cellId) {
		Path dir = Paths.get(IMAGE_BASE_DIR + cellId);
		try {
			if (!Files.exists(dir)) {
				Files.createDirectories(dir);
			}
			Path filePath = dir.resolve("photo.jpg");
			Files.deleteIfExists(filePath);

			BufferedImage original = ImageIO.read(file.getInputStream());
			if (original == null) {
				throw new CustomException(ErrorCode.INVALID_IMAGE_FORMAT);
			}

			// 최대 800px로 리사이즈
			int maxSize = 800;
			int w = original.getWidth(), h = original.getHeight();
			float scale = Math.min((float) maxSize / w, (float) maxSize / h);
			if (scale < 1.0f) {
				int nw = Math.round(w * scale), nh = Math.round(h * scale);
				Image resized = original.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
				BufferedImage buf = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
				Graphics2D g = buf.createGraphics();
				g.drawImage(resized, 0, 0, null);
				g.dispose();
				original = buf;
			}

			// JPEG 80% 품질로 저장
			try (OutputStream os = Files.newOutputStream(filePath)) {
				ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
				ImageOutputStream ios = ImageIO.createImageOutputStream(os);
				writer.setOutput(ios);
				ImageWriteParam param = writer.getDefaultWriteParam();
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(0.8f);
				writer.write(null, new IIOImage(original, null, null), param);
				writer.dispose();
				ios.close();
			}

			return filePath.toString();
		} catch (IOException e) {
			log.error("[BingoV1] 이미지 저장 실패: cellId={}", cellId, e);
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	private void deleteImageFile(String imagePath) {
		if (imagePath == null || imagePath.isBlank()) return;
		try {
			Files.deleteIfExists(Paths.get(imagePath));
		} catch (IOException e) {
			log.warn("[BingoV1] 이미지 삭제 실패: path={}", imagePath, e);
		}
	}

	private void saveActivityLog(Team team, BingoCellV1 cell, ActivityLogStatusV1 status, String message) {
		activityLogV1Repository.save(BingoActivityLogV1.builder()
			.team(team)
			.cell(cell)
			.status(status)
			.message(message)
			.timestamp(LocalDateTime.now())
			.build());
	}
}
