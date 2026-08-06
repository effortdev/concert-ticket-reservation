package com.effortdev.ticketing.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password; // LOCAL 로그인 전용 (암호화 저장), OAuth 유저는 null

    private String nickname;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider; // LOCAL, GOOGLE, KAKAO

    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String email, String password, String nickname, AuthProvider provider, String providerId, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role == null ? Role.USER : role;
    }

    public enum AuthProvider {
        LOCAL, GOOGLE, KAKAO
    }

    public enum Role {
        USER, ADMIN
    }
}