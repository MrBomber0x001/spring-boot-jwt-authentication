package com.meska.book_reads.service;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.ArrayList;

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;

    public OAuth2SuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        //log
        String email =  oAuth2User.getAttribute("email");


        UserDetails userDetails = User.builder()
                .username(email)
                .password("") // No password needed for OAuth users
                .authorities(new ArrayList<>())
                .build();

        String jwt = jwtService.generateToken(userDetails);

        response.sendRedirect("/api/auth/oauth-success?token=" + jwt);

    }
}