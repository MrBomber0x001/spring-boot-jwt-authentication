package com.meska.book_reads.service;

import com.meska.book_reads.dtos.OAuthUserInfo;
import com.meska.book_reads.dtos.SignUpRequest;
import com.meska.book_reads.dtos.UserResponse;
import com.meska.book_reads.entity.AuthProvider;
import com.meska.book_reads.entity.User;
import com.meska.book_reads.exceptions.UserAlreadyExistsException;
import com.meska.book_reads.repository.UserRepo;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;


    // For email/password signup
    public UserResponse createUser(SignUpRequest signUpRequest) {
        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .provider(AuthProvider.LOCAL)
                .build();

        user = userRepo.save(user);
        return new UserResponse(user.getId(), user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>());
    }

    // For OAuth users
    public User processOAuthUser(OAuthUserInfo userInfo) {
        return userRepo.findByEmail(userInfo.getEmail())
                .map(existingUser -> updateExistingUser(existingUser, userInfo))
                .orElseGet(() -> createOAuthUser(userInfo));
    }

    private User updateExistingUser(User existingUser, OAuthUserInfo userInfo) {
        if (existingUser.getProvider() == AuthProvider.LOCAL) {
            log.warn("Local user {} attempting OAuth login with {}",
                    existingUser.getEmail(), userInfo.getProvider());
            throw new OAuth2AuthenticationException("Email already registered locally");
        }

        existingUser.setName(userInfo.getName());
        return userRepo.save(existingUser);
    }

    private User createOAuthUser(OAuthUserInfo userInfo) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .provider(userInfo.getProvider())
                .providerId(userInfo.getProviderId())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .build();

        return userRepo.save(user);
    }

}
