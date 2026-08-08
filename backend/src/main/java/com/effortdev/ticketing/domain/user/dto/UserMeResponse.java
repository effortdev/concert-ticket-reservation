package com.effortdev.ticketing.domain.user.dto;

import com.effortdev.ticketing.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserMeResponse {
    private final Long userId;
    private final String nickname;
    private final String role;

    public UserMeResponse(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname();
        this.role = user.getRole().name();
    }
}