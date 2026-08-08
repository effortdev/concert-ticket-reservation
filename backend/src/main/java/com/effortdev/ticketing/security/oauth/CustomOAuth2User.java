package com.effortdev.ticketing.security.oauth;

import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2User oAuth2User;
    private final Long userId;

    public CustomOAuth2User(OAuth2User oAuth2User, Long userId) {
        this.oAuth2User = oAuth2User;
        this.userId = userId;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        return oAuth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return oAuth2User.getName();
    }
}