package com.likelion13th.Welcomekit_BE.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.likelion13th.Welcomekit_BE.domain.dto.ApiResponse;
import com.likelion13th.Welcomekit_BE.domain.dto.request.CreateQnaCommentRequest;
import com.likelion13th.Welcomekit_BE.domain.dto.response.QnaCommentResponse;
import com.likelion13th.Welcomekit_BE.exception.QnaException;
import com.likelion13th.Welcomekit_BE.service.QnaCommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/qna/comments")
@RequiredArgsConstructor
@Tag(name = "문의 댓글", description = "문의(QnA) 게시글에 대한 댓글 작성/조회/삭제 API. 본인 글이거나 운영진만 작성·조회할 수 있습니다.")
public class QnaCommentController {

    private final QnaCommentService service;

    @Operation(summary = "문의 댓글 작성", description = "본인 글이거나 운영진만 댓글을 작성할 수 있습니다.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @PostMapping
    public ResponseEntity<ApiResponse<QnaCommentResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateQnaCommentRequest request
    ){
        QnaCommentResponse data = service.createComment(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("S201", "댓글이 등록되었습니다", data));
    }

    @Operation(summary = "문의 댓글 목록 조회", description = "본인 글이거나 운영진만 조회할 수 있습니다.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @GetMapping("/{qnaId}")
    public ApiResponse<List<QnaCommentResponse>> get(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long qnaId
    ){
        List<QnaCommentResponse> data = service.getComments(userDetails.getUsername(), qnaId);
        return ApiResponse.success("S200", "댓글 목록 조회에 성공했습니다", data);
    }

    @Operation(summary = "문의 댓글 삭제", description = "작성자 본인이 자신의 댓글을 삭제합니다.",
            security = @SecurityRequirement(name = "Bearer Authentication"))
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ){
        service.deleteComment(userDetails.getUsername(), id);
        return ApiResponse.success("S200", "댓글이 삭제되었습니다", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
        String[] parts = fieldError.getDefaultMessage().split(":", 2);
        return ResponseEntity.badRequest().body(ApiResponse.error(parts[0], parts[1]));
    }

    @ExceptionHandler(QnaException.class)
    public ResponseEntity<ApiResponse<Object>> handleQnaException(QnaException ex) {
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleError(Exception ex) {
        log.error("[문의 댓글] 처리 실패", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("E500", "댓글 처리 중 오류가 발생했습니다"));
    }
}
