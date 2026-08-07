package com.effortdev.ticketing.domain.user.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private final String accessToken;
    private final String refreshToken;
    private final Long userId;
    private final String nickname;

    public LoginResponse(String accessToken, String refreshToken, Long userId, String nickname) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.nickname = nickname;
    }
}