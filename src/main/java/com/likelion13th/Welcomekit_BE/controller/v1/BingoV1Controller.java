package com.likelion13th.Welcomekit_BE.controller.v1;

import com.likelion13th.Welcomekit_BE.service.v1.BingoV1Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bingo")
@Tag(name = "14기 빙고 V1", description = "14기 전용 글로벌 5x5 빙고 보드 API. 모든 팀이 하나의 보드를 공유하며 미션 사진을 업로드해 12시간 독점을 겨룹니다.")
public class BingoV1Controller {

	private final BingoV1Service bingoV1Service;

	// ──────────────────────────────────────────────────────────
	// 1. 글로벌 보드 조회
	// ──────────────────────────────────────────────────────────

	@Operation(
		summary = "글로벌 빙고판 조회 (로그인 불필요)",
		description = """
			전체 팀이 공유하는 5×5 글로벌 빙고판의 25개 칸 상태를 조회합니다.

			- **EMPTY**: 아무 팀도 도전하지 않은 칸
			- **PROCESSING**: 한 팀이 사진을 올리고 12시간 타이머가 진행 중인 칸
			- **OCCUPIED**: 12시간을 버텨 점유 확정된 칸
			"""
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "cells": [
					    { "cellId": 1, "status": "OCCUPIED",   "imageUrl": "/api/v1/bingo/image/1",  "teamName": "멋사팀" },
					    { "cellId": 2, "status": "PROCESSING", "imageUrl": "/api/v1/bingo/image/2",  "teamName": "아기사자팀" },
					    { "cellId": 3, "status": "EMPTY",      "imageUrl": null,                     "teamName": null },
					    { "cellId": 4, "status": "EMPTY",      "imageUrl": null,                     "teamName": null }
					  ]
					}
					""")))
	})
	@GetMapping("/global")
	public ResponseEntity<?> getGlobalBoard() {
		return ResponseEntity.ok(bingoV1Service.getGlobalBoard());
	}

	// ──────────────────────────────────────────────────────────
	// 2. 글로벌 칸 상세 조회
	// ──────────────────────────────────────────────────────────

	@Operation(
		summary = "글로벌 빙고 칸 상세 조회 (로그인 불필요)",
		description = "특정 칸의 미션 정보, 점유 팀, 남은 시간(HH:mm:ss)을 조회합니다. cellId는 1~25 사이 값입니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				examples = {
					@ExampleObject(name = "PROCESSING 상태 예시", value = """
						{
						  "cellId": 2,
						  "status": "PROCESSING",
						  "mission": {
						    "title": "팀원 전체 손가락 하트 단체 사진",
						    "description": "학교 건물이 보이도록 찍어야 인정됩니다."
						  },
						  "displayData": {
						    "imageUrl": "/api/v1/bingo/image/2",
						    "teamName": "아기사자팀",
						    "remainingTime": "11:23:45",
						    "statusLabel": "진행 중"
						  }
						}
						"""),
					@ExampleObject(name = "EMPTY 상태 예시", value = """
						{
						  "cellId": 3,
						  "status": "EMPTY",
						  "mission": {
						    "title": "동방/카페 오프라인 모각코",
						    "description": "팀원 전원이 노트북을 펼치고 코딩하는 사진을 찍으세요."
						  },
						  "displayData": {
						    "imageUrl": null,
						    "teamName": null,
						    "remainingTime": null,
						    "statusLabel": "도전 가능"
						  }
						}
						""")
				})),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 cellId",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{ "code": "CELL_NOT_FOUND", "message": "Cell does not exist" }
					""")))
	})
	@GetMapping("/global/{cellId}")
	public ResponseEntity<?> getGlobalCellDetail(
		@Parameter(description = "조회할 셀 ID (1~25)", example = "1", required = true)
		@PathVariable Long cellId) {
		return ResponseEntity.ok(bingoV1Service.getGlobalCellDetail(cellId));
	}

	// ──────────────────────────────────────────────────────────
	// 3. 팀 빙고판 조회
	// ──────────────────────────────────────────────────────────

	@Operation(
		summary = "우리 팀 빙고판 조회",
		description = """
			우리 팀 시점에서 25개 칸 상태를 조회합니다.

			`borderType` 값 의미:
			- `NONE` — 도전 가능 (EMPTY)
			- `PROCESSING_OURS` — 우리 팀이 12시간 타이머 진행 중
			- `PROCESSING_OTHERS` — 다른 팀이 타이머 진행 중
			- `BLACK` — 우리 팀 점유 확정
			- `GRAY` — 다른 팀 점유 확정
			""",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "teamName": "멋사팀",
					  "cells": [
					    { "cellId": 1, "status": "OCCUPIED",   "isOurTeam": true,  "borderType": "BLACK",             "imageUrl": "/api/v1/bingo/image/1" },
					    { "cellId": 2, "status": "PROCESSING", "isOurTeam": true,  "borderType": "PROCESSING_OURS",   "imageUrl": "/api/v1/bingo/image/2" },
					    { "cellId": 3, "status": "PROCESSING", "isOurTeam": false, "borderType": "PROCESSING_OTHERS", "imageUrl": "/api/v1/bingo/image/3" },
					    { "cellId": 4, "status": "OCCUPIED",   "isOurTeam": false, "borderType": "GRAY",              "imageUrl": "/api/v1/bingo/image/4" },
					    { "cellId": 5, "status": "EMPTY",      "isOurTeam": false, "borderType": "NONE",              "imageUrl": null }
					  ]
					}
					"""))),
		@ApiResponse(responseCode = "400", description = "팀에 배정되지 않은 유저",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{ "code": "TEAM_NOT_ASSIGNED", "message": "팀에 배정되지 않은 사용자입니다." }
					""")))
	})
	@GetMapping("/team")
	public ResponseEntity<?> getTeamBoard(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(bingoV1Service.getTeamBoard(userDetails.getUsername()));
	}

	// ──────────────────────────────────────────────────────────
	// 4. 팀 미션 상세 조회
	// ──────────────────────────────────────────────────────────

	@Operation(
		summary = "팀 시점 빙고 칸 상세 조회 (카운트다운 포함)",
		description = "우리 팀 시점의 특정 칸 상세 정보. `remainingSeconds`는 초 단위 실시간 카운트다운입니다.",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				examples = {
					@ExampleObject(name = "PROCESSING 상태 (카운트다운 포함)", value = """
						{
						  "cellId": 2,
						  "status": "PROCESSING",
						  "mission": {
						    "title": "팀원 전체 손가락 하트 단체 사진",
						    "description": "학교 건물이 보이도록 찍어야 인정됩니다."
						  },
						  "displayData": {
						    "teamName": "멋사팀",
						    "imageUrl": "/api/v1/bingo/image/2",
						    "remainingSeconds": 41025,
						    "isConfirmed": false
						  },
						  "updatedAt": "2026-03-19T03:00:00"
						}
						"""),
					@ExampleObject(name = "OCCUPIED 상태 (점유 확정)", value = """
						{
						  "cellId": 1,
						  "status": "OCCUPIED",
						  "mission": {
						    "title": "편의점 간식 조합 인증",
						    "description": "세 가지 이상의 간식을 섞어 먹는 팀원 전체 사진을 찍으세요."
						  },
						  "displayData": {
						    "teamName": "멋사팀",
						    "imageUrl": "/api/v1/bingo/image/1",
						    "remainingSeconds": null,
						    "isConfirmed": true
						  },
						  "updatedAt": "2026-03-18T15:00:00"
						}
						""")
				}))
	})
	@GetMapping("/team/{cellId}")
	public ResponseEntity<?> getTeamCellDetail(
		@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(description = "조회할 셀 ID (1~25)", example = "1", required = true)
		@PathVariable Long cellId) {
		return ResponseEntity.ok(bingoV1Service.getTeamCellDetail(userDetails.getUsername(), cellId));
	}

	// ──────────────────────────────────────────────────────────
	// 5. 미션 사진 업로드 (독점 시작)
	// ──────────────────────────────────────────────────────────

	@Operation(
		summary = "미션 사진 업로드 — 12시간 독점 시작",
		description = """
			사진을 업로드하면 해당 칸에 대한 12시간 독점 타이머가 시작됩니다.

			**케이스별 동작:**
			- 칸이 **EMPTY** → 바로 PROCESSING 전환, 타이머 시작
			- 칸이 **PROCESSING** (다른 팀) → 기존 팀 VOID 처리 후 내 팀으로 교체, 새 타이머 시작 (가로채기)
			- 칸이 **PROCESSING** (내 팀) → 오류 반환. 사진 수정은 **PATCH** 엔드포인트 사용
			- 칸이 **OCCUPIED** → 오류 반환 (이미 점유 확정)

			**Request**: `multipart/form-data`, 파라미터 이름: `image`
			""",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "업로드 성공, 12시간 타이머 시작",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "message": "성공적으로 업로드되었습니다. 12시간의 독점이 시작됩니다.",
					  "cellId": 5,
					  "startTime": "2026-03-19T03:10:00",
					  "endTime":   "2026-03-19T15:10:00"
					}
					"""))),
		@ApiResponse(responseCode = "400", description = "본인 팀이 진행 중인 칸 — PATCH 사용 필요",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{ "code": "USE_PATCH_FOR_UPDATE", "message": "본인 팀의 진행 중인 사진은 PATCH 엔드포인트로 수정해주세요." }
					"""))),
		@ApiResponse(responseCode = "409", description = "이미 점유 확정된 칸",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{ "code": "CELL_ALREADY_OCCUPIED", "message": "해당 칸은 이미 점유 완료되었습니다." }
					""")))
	})
	@PostMapping(value = "/team/{cellId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadMission(
		@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(description = "업로드할 셀 ID (1~25)", example = "5", required = true)
		@PathVariable Long cellId,
		@RequestParam("image") MultipartFile image) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(bingoV1Service.uploadMission(userDetails.getUsername(), cellId, image));
	}

	// ──────────────────────────────────────────────────────────
	// 6. 업로드 사진 수정 (12시간 이내)
	// ──────────────────────────────────────────────────────────

	@Operation(
		summary = "업로드 사진 수정 — 12시간 이내 본인 팀만 가능",
		description = """
			이미 업로드한 사진을 교체합니다. **타이머는 리셋되지 않습니다.**

			**실패 조건:**
			- 해당 칸이 PROCESSING이 아닌 경우
			- 요청 팀이 현재 소유 팀이 아닌 경우
			- 12시간 타이머가 이미 만료된 경우 (스케줄러가 아직 OCCUPIED 처리 전이어도 실질 만료 시 오류)
			""",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "사진 수정 성공 (타이머 유지)",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "message": "사진이 성공적으로 수정되었습니다. 타이머는 유지됩니다.",
					  "cellId": 5,
					  "startTime": "2026-03-19T03:10:00",
					  "endTime":   "2026-03-19T15:10:00"
					}
					"""))),
		@ApiResponse(responseCode = "403", description = "소유 팀이 아님 또는 PROCESSING 상태 아님",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{ "code": "NOT_CELL_OWNER", "message": "해당 칸의 소유 팀이 아니거나 진행 중인 상태가 아닙니다." }
					"""))),
		@ApiResponse(responseCode = "400", description = "12시간 타이머 이미 만료",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{ "code": "UPDATE_WINDOW_EXPIRED", "message": "12시간 독점 기간이 이미 만료되었습니다." }
					""")))
	})
	@PatchMapping(value = "/team/{cellId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateMission(
		@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(description = "수정할 셀 ID (1~25)", example = "5", required = true)
		@PathVariable Long cellId,
		@RequestParam("image") MultipartFile image) {
		return ResponseEntity.ok(bingoV1Service.updateMission(userDetails.getUsername(), cellId, image));
	}

	// ──────────────────────────────────────────────────────────
	// 7. 이미지 서빙
	// ──────────────────────────────────────────────────────────

	@Operation(
		summary = "셀 이미지 다운로드 (로그인 불필요)",
		description = "imageUrl 필드(`/api/v1/bingo/image/{cellId}`)를 통해 이미지를 직접 제공하는 엔드포인트입니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "JPEG 이미지 반환"),
		@ApiResponse(responseCode = "400", description = "이미지가 없는 셀",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{ "code": "IMAGE_NOT_FOUND", "message": "IMAGE NOT FOUND" }
					""")))
	})
	@GetMapping("/image/{cellId}")
	public ResponseEntity<byte[]> getCellImage(
		@Parameter(description = "이미지를 가져올 셀 ID", example = "1", required = true)
		@PathVariable Long cellId) throws IOException {
		byte[] data = bingoV1Service.getCellImage(cellId);
		return ResponseEntity.ok()
			.contentType(MediaType.IMAGE_JPEG)
			.body(data);
	}
}
