package com.effortdev.ticketing.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * TODO:
 *  - createAccessToken(userId, role)
 *  - createRefreshToken(userId)
 *  - validateToken(token)
 *  - getUserIdFromToken(token)
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expire-ms}")
    private long accessTokenExpireMs;

    @Value("${jwt.refresh-token-expire-ms}")
    private long refreshTokenExpireMs;

    // TODO: 구현 예정
}
