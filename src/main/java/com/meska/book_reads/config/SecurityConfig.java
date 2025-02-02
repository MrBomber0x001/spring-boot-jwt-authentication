package com.meska.book_reads.config;

import com.meska.book_reads.service.JwtAuthenticationFilter;
import com.meska.book_reads.service.JwtService;
import com.meska.book_reads.service.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oauth2SuccessHandler;

    private final JwtService jwtService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrfConfig -> csrfConfig.disable());
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/signup",
                                "/api/auth/**",
                                "/api/auth/oauth/**",
                                "/api/auth/oauth-success",
                                "/api/auth/oauth-error",
                                "/oauth2/**",
                                "/login/oauth2/code/**"
                                ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> {
                    oauth2
                                    .successHandler(oauth2SuccessHandler);
                    oauth2.failureHandler((request, response, exception) -> {
                        log.error("OAuth2 authentication failed", exception);
                        String encodedError = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8);
                        response.sendRedirect("/api/auth/oauth-error?error=" + encodedError);
                    });
                        }
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

}
