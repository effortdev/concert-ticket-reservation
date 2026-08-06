package com.effortdev.ticketing.domain.user.dto;

import com.effortdev.ticketing.domain.user.entity.User;
import lombok.Getter;

@Getter
public class SignupResponse {

    private final Long userId;
    private final String email;
    private final String nickname;

    public SignupResponse(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
    }
}