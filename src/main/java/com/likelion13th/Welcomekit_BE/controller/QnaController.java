package com.likelion13th.Welcomekit_BE.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.likelion13th.Welcomekit_BE.domain.dto.ApiResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateQnaRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.QnaDetailResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.response.QnaListResponse;
import com.likelion13th.Welcomekit_BE.exception.QnaException;
import com.likelion13th.Welcomekit_BE.service.QnaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/qna")
@RequiredArgsConstructor
@Tag(name = "문의", description = "문의(QnA) 게시글 작성/목록/상세/삭제 API. 본인 글이거나 운영진만 조회할 수 있습니다.")
public class QnaController {

    private final QnaService qnaService;

    @Operation(summary = "문의글 작성", description = "새 문의(QnA) 게시글을 작성합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping
    public ResponseEntity<ApiResponse<QnaDetailResponse>> createQna(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateQnaRequest request
    ){
        QnaDetailResponse data = qnaService.createQna(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("S201", "문의글이 등록되었습니다", data));
    }

    @Operation(summary = "문의글 목록 조회",
            description = "운영진은 전체 문의글을, 일반 부원은 본인이 작성한 문의글만 최신순으로 페이지네이션 조회합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping
    public ApiResponse<QnaListResponse> getAllQna(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "페이지 번호 (0-based, 선택, 기본값 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 개수 (선택, 기본값 10)", example = "10")
            @RequestParam(defaultValue = "10") int size
    ){
        QnaListResponse data = qnaService.getQnaList(userDetails.getUsername(), page, size);
        return ApiResponse.success("S200", "문의글 목록 조회에 성공했습니다", data);
    }

    @Operation(summary = "문의글 상세 조회", description = "본인 글이거나 운영진만 조회할 수 있습니다.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/{id}")
    public ApiResponse<QnaDetailResponse> getQna(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ){
        QnaDetailResponse data = qnaService.getQna(userDetails.getUsername(), id);
        return ApiResponse.success("S200", "문의글 조회에 성공했습니다", data);
    }

    @Operation(summary = "문의글 삭제", description = "작성자 본인이 자신의 문의글을 삭제합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQna(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ){
        qnaService.deleteQna(userDetails.getUsername(), id);
        return ApiResponse.success("S200", "문의글이 삭제되었습니다", null);
    }

    /** DTO 필드 검증 실패: message 에 담긴 "코드:메시지" 를 그대로 응답 code/message 로 사용한다. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        String[] parts = fieldError.getDefaultMessage().split(":", 2);
        return ResponseEntity.badRequest().body(ApiResponse.error(parts[0], parts[1]));
    }

    /** 권한/존재 여부 등 DTO 검증만으로 표현 못 하는 비즈니스 규칙 위반. */
    @ExceptionHandler(QnaException.class)
    public ResponseEntity<ApiResponse<Object>> handleQnaException(QnaException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleError(Exception ex) {
        log.error("[문의] 처리 실패", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("E500", "문의글 처리 중 오류가 발생했습니다"));
    }
}
