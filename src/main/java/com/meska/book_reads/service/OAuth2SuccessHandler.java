package com.meska.book_reads.service;


import com.meska.book_reads.dtos.OAuthUserInfo;
import com.meska.book_reads.entity.AuthProvider;
import com.meska.book_reads.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.debug("Starting OAuth2 authentication success handling");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.debug("OAuth2 user attributes: {}", attributes);
        // Extract provider from registration ID
        // Get the registration ID from the authentication object
        String registrationId = ((OAuth2AuthenticationToken) authentication)
                .getAuthorizedClientRegistrationId()
                .toUpperCase();

        log.debug("OAuth2 registerationId attributes: {}", registrationId);
        log.debug("Processing OAuth2 login for provider: {}", registrationId);


        try {
            OAuthUserInfo userInfo = extractUserInfo(attributes, registrationId);
            User user = userService.processOAuthUser(userInfo);
            UserDetails userDetails = userService.loadUserByUsername(user.getEmail());

            String token = jwtService.generateToken(userDetails);
            String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

            // Use UriComponentsBuilder to safely construct the redirect URL
            String redirectUrl = UriComponentsBuilder.fromPath("/api/auth/oauth-success")
                    .queryParam("token", encodedToken)
                    .build()
                    .encode()
                    .toUriString();

            log.debug("Redirecting to success URL with token");
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            log.error("Error during OAuth2 success handling", e);
            String encodedError = URLEncoder.encode("Authentication failed", StandardCharsets.UTF_8);
            response.sendRedirect("/api/auth/oauth-error?error=" + encodedError);
        }
    }

    private OAuthUserInfo extractUserInfo(Map<String, Object> attributes, String provider) {
        if (provider.equals("GITHUB")) {
            return OAuthUserInfo.builder()
                    .email((String) attributes.get("email"))
                    .name((String) attributes.get("name"))
                    .providerId(attributes.get("id").toString())
                    .provider(AuthProvider.GITHUB)
                    .build();
        } else {
            return OAuthUserInfo.builder()
                    .email((String) attributes.get("email"))
                    .name((String) attributes.get("name"))
                    .providerId((String) attributes.get("sub"))
                    .provider(AuthProvider.GOOGLE)
                    .build();
        }
    }
}