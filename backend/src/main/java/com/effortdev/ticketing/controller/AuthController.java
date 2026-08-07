package com.effortdev.ticketing.controller;

import com.effortdev.ticketing.common.response.ApiResponse;
import com.effortdev.ticketing.domain.user.dto.LoginRequest;
import com.effortdev.ticketing.domain.user.dto.LoginResponse;
import com.effortdev.ticketing.domain.user.dto.SignupRequest;
import com.effortdev.ticketing.domain.user.dto.SignupResponse;
import com.effortdev.ticketing.domain.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.success(authService.signup(request));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    // TODO: POST /api/auth/reissue - 리프레시 토큰으로 액세스 토큰 재발급
    // OAuth(Google/Kakao)는 Spring Security oauth2Login 흐름으로 별도 처리 예정

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("ok");
    }
}