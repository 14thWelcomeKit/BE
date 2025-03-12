package com.likelion13th.Welcomekit_BE.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true)
    private String name;

    private String studentnumber;
    private String studentid;
    private String password;

    private Boolean isAdmin=false;

    @Column(nullable=true)
    private Integer team;

    @Column(nullable=true)
    private String track;

}

