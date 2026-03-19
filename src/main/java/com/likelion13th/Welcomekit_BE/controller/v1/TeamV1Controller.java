package com.likelion13th.Welcomekit_BE.controller.v1;

import com.likelion13th.Welcomekit_BE.service.v1.BingoV1Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/team")
@Tag(name = "14기 팀 V1", description = "14기 팀 정보 및 빙고 활동 로그 API")
public class TeamV1Controller {

	private final BingoV1Service bingoV1Service;

	@Operation(
		summary = "내 팀 정보 및 순위 조회",
		description = """
			내 팀의 이름, 팀원 목록, 점유한 칸 수, 전체 순위를 조회합니다.

			- `occupiedCount`: OCCUPIED(확정) 상태인 칸 수 (PROCESSING 제외)
			- `ranking`: OCCUPIED 칸 수 기준 내림차순. 공동 순위 지원 (예: 1, 2, 2, 4...)
			""",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "teamId": 10,
					  "teamName": "멋사팀",
					  "members": ["재영", "다은", "호준", "지민"],
					  "progress": {
					    "occupiedCount": 5,
					    "totalCells": 25,
					    "ranking": 2
					  }
					}
					"""))),
		@ApiResponse(responseCode = "400", description = "팀에 배정되지 않은 유저",
			content = @Content(mediaType = "application/json",
				examples = @ExampleObject(value = """
					{ "code": "TEAM_NOT_ASSIGNED", "message": "팀에 배정되지 않은 사용자입니다." }
					""")))
	})
	@GetMapping("/me")
	public ResponseEntity<?> getTeamInfo(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(bingoV1Service.getTeamInfo(userDetails.getUsername()));
	}

	@Operation(
		summary = "내 팀 활동 로그 조회 (최신순)",
		description = """
			내 팀에서 발생한 **SUCCESS** / **VOID** 이벤트 로그를 최신순으로 조회합니다.

			- `SUCCESS`: 12시간 타이머를 버텨 해당 칸 점유에 성공
			- `VOID`: 다른 팀의 중복 업로드로 인해 독점이 무효화됨
			""",
		security = @SecurityRequirement(name = "Bearer Authentication")
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(mediaType = "application/json",
				examples = {
					@ExampleObject(name = "SUCCESS + VOID 혼재 예시", value = """
						{
						  "activities": [
						    {
						      "cellId": 3,
						      "status": "SUCCESS",
						      "message": "3번 칸 독점에 성공하여 영토를 획득했습니다!",
						      "timestamp": "2026-03-19T15:00:00"
						    },
						    {
						      "cellId": 7,
						      "status": "VOID",
						      "message": "7번 칸 독점이 다른 팀의 업로드로 무효화되었습니다.",
						      "timestamp": "2026-03-19T09:00:00"
						    },
						    {
						      "cellId": 1,
						      "status": "SUCCESS",
						      "message": "1번 칸 독점에 성공하여 영토를 획득했습니다!",
						      "timestamp": "2026-03-18T18:00:00"
						    }
						  ]
						}
						"""),
					@ExampleObject(name = "활동 내역 없음", value = """
						{ "activities": [] }
						""")
				}))
	})
	@GetMapping("/activities")
	public ResponseEntity<?> getTeamActivities(
		@AuthenticationPrincipal UserDetails userDetails) {
		return ResponseEntity.ok(bingoV1Service.getTeamActivities(userDetails.getUsername()));
	}
}
