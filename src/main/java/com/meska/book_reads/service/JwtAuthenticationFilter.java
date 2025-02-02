package com.meska.book_reads.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meska.book_reads.exceptions.GlobalExceptionHandler;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       try{
           // validation check on token presence
           final String authHeader = request.getHeader("Authorization");
           if (authHeader == null || !authHeader.startsWith("Bearer ")) {
               filterChain.doFilter(request, response);
               return;
           }

           // extract token
           String jwt = authHeader.substring(7);
           String userEmail = jwtService.extractUsername(jwt);

           if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
               UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
               if (jwtService.validateToken(jwt, userDetails)) {
                   UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                           userDetails, null, userDetails.getAuthorities());
                   authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                   SecurityContextHolder.getContext().setAuthentication(authToken);
               }
           }
           filterChain.doFilter(request, response);

       } catch (JwtException | AuthenticationException ex){
//           sendErrorResponse(response, ex);
       } catch (Exception ex){
//           sendErrorResponse(response, new AuthenticationServiceException("Authentication failed", ex));
       }
    }

//    public void sendErrorResponse(HttpServletResponse response, Exception ex) throws IOException {
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//        String errCode = 'AUTH_002';
//        String message = 'Authentication error';
//
//        if(ex instanceof BadCredentialsException){
//            errCode = "AUTH_001";
//            message = "Invalid Credentials";
//        } else if(ex instanceof ExpiredJwtException){
//            message = "Token expired";
//        }
//
//        new ObjectMapper().writeValue(
//                response.getOutputStream(),
//                new GlobalExceptionHandler().ErrorResponse(message, errCode)
//        );
//    }
}
