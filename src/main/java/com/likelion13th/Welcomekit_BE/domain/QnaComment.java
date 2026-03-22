package com.likelion13th.Welcomekit_BE.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    private Boolean isAdminComment;

    @ManyToOne
    @JoinColumn(name = "qna_id")
    private Qna qna;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}