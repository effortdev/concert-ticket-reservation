package com.effortdev.ticketing.controller;

import com.effortdev.ticketing.common.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // TODO: POST /api/auth/login - 일반 로그인, JWT 발급
    // TODO: POST /api/auth/reissue - 리프레시 토큰으로 액세스 토큰 재발급
    // OAuth(Google/Kakao)는 Spring Security oauth2Login 흐름으로 별도 처리 예정

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("ok");
    }
}
