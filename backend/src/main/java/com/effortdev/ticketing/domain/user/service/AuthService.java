package com.effortdev.ticketing.domain.user.service;

import com.effortdev.ticketing.common.exception.CustomException;
import com.effortdev.ticketing.domain.user.dto.SignupRequest;
import com.effortdev.ticketing.domain.user.dto.SignupResponse;
import com.effortdev.ticketing.domain.user.entity.User;
import com.effortdev.ticketing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new CustomException(HttpStatus.CONFLICT, "이미 가입된 이메일입니다.");
                });

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .provider(User.AuthProvider.LOCAL)
                .role(User.Role.USER)
                .build();

        User saved = userRepository.save(user);
        return new SignupResponse(saved);
    }
}