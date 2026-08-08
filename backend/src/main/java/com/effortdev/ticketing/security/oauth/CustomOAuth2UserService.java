package com.effortdev.ticketing.security.oauth;

import com.effortdev.ticketing.domain.user.entity.User;
import com.effortdev.ticketing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByProviderAndProviderId(User.AuthProvider.GOOGLE, providerId)
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .nickname(name)
                                .provider(User.AuthProvider.GOOGLE)
                                .providerId(providerId)
                                .role(User.Role.USER)
                                .build()
                ));

        return new CustomOAuth2User(oAuth2User, user.getId());
    }
}