package com.likelionknu.applyserver.auth.service;

import com.likelionknu.applyserver.auth.data.dto.response.TokenResponseDto;
import com.likelionknu.applyserver.auth.data.entity.GoogleProfile;
import com.likelionknu.applyserver.auth.data.entity.Profile;
import com.likelionknu.applyserver.auth.data.entity.User;
import com.likelionknu.applyserver.auth.data.enums.Role;
import com.likelionknu.applyserver.auth.data.repository.UserRepository;
import com.likelionknu.applyserver.auth.exception.GoogleAuthenticaionFailedException;
import com.likelionknu.applyserver.common.security.AuthenticationToken;
import com.likelionknu.applyserver.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private GoogleProfile getGoogleProfile(String code) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String tokenUrl = "https://oauth2.googleapis.com/token";
            Map<String, String> params = new HashMap<>();
            params.put("code", code);
            params.put("client_id", clientId);
            params.put("client_secret", clientSecret);
            params.put("redirect_uri", redirectUri);
            params.put("grant_type", "authorization_code");

            ResponseEntity<Map<String, Object>> tokenResponse = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, new HttpEntity<>(params), new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> tokenBody = tokenResponse.getBody();
            if (tokenBody == null || !tokenBody.containsKey("access_token")) {
                log.error("[getGoogleProfile] Google Access Token 발급 실패");
                throw new GoogleAuthenticaionFailedException();
            }
            String accessToken = tokenBody.get("access_token").toString();

            String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> attributes = userResponse.getBody();
            if (attributes == null) {
                log.error("[getGoogleProfile] Google 소셜 프로필 조회 실패");
                throw new GoogleAuthenticaionFailedException();
            }

            return GoogleProfile.builder()
                    .email((String) attributes.get("email"))
                    .name((String) attributes.get("name"))
                    .profileUrl((String) attributes.get("picture"))
                    .build();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new GoogleAuthenticaionFailedException();
        }
    }

    private Authentication createAuthentication(User user) {
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(user.getRole().toString())
        );

        return new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
    }

    private User registerNewUser(GoogleProfile googleProfile) {
        User newUser = new User();
        newUser.setEmail(googleProfile.getEmail());
        newUser.setName(googleProfile.getName());
        newUser.setProfileUrl(googleProfile.getProfileUrl());
        newUser.setRole(Role.USER);
        newUser.setLastAccessAt(LocalDateTime.now());

        Profile profile = Profile.builder()
                .user(newUser)
                .build();

        newUser.setProfile(profile);

        userRepository.save(newUser);

        return newUser;
    }

    public TokenResponseDto userSocialSignIn(String code) {
        GoogleProfile googleProfile = getGoogleProfile(code);
        User user = userRepository.findByEmail(googleProfile.getEmail());

        if(user == null) {
            log.info("[userSocialSignIn] 새로운 사용자: {}", googleProfile.getEmail());
            user = registerNewUser(googleProfile);
        } else {
            log.info("[userSocialSignIn] 기존 가입된 사용자: {}", googleProfile.getEmail());
        }

        Authentication authentication = createAuthentication(user);
        AuthenticationToken authenticationToken = jwtTokenProvider.generateToken(authentication);
        return TokenResponseDto.builder()
                .accessToken(authenticationToken.getAccessToken())
                .refreshToken(authenticationToken.getRefreshToken())
                .name(user.getName())
                .role(user.getRole().toString())
                .build();
    }

    public void userLogout(String email) {
        log.info("[userLogout] 사용자 로그아웃: {}", email);
        jwtTokenProvider.expireUsersToken(email);
    }

    public TokenResponseDto reissue(String refreshToken) {
        jwtTokenProvider.validateRefreshToken(refreshToken);

        String email = jwtTokenProvider.getSubject(refreshToken);
        User user = userRepository.findByEmail(email);
        Authentication authentication = createAuthentication(user);
        AuthenticationToken authenticationToken = jwtTokenProvider.generateToken(authentication);
        log.info("[reissue] 사용자 JWT 재발급 완료: {}", user.getEmail());

        return TokenResponseDto.builder()
                .accessToken(authenticationToken.getAccessToken())
                .refreshToken(authenticationToken.getRefreshToken())
                .name(user.getName())
                .role(user.getRole().toString())
                .build();
    }
}