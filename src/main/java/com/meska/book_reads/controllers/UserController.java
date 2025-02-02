package com.meska.book_reads.controllers;

import com.meska.book_reads.dtos.JwtResponse;
import com.meska.book_reads.dtos.LoginRequest;
import com.meska.book_reads.dtos.SignUpRequest;
import com.meska.book_reads.dtos.UserResponse;
import com.meska.book_reads.service.JwtService;
import com.meska.book_reads.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(userService.createUser(signUpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login( @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @GetMapping("/me")
    public String currentUser(Principal principal){
        return principal.getName(); // returned "mrbomberboy123@gmail.com" which actually the email
    }

    @GetMapping("/profile")
    public UserDetails getProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetails) authentication.getPrincipal();
        /**
         * {
         *     "password": "$2a$10$ttQXbHoEucxUsr/9Rt1mMeJdG38cKJSARFsB3K/J2ZXKg5endhfMG",
         *     "username": "mrbomberboy12345@gmail.com",
         *     "authorities": [],
         *     "accountNonExpired": true,
         *     "accountNonLocked": true,
         *     "credentialsNonExpired": true,
         *     "enabled": true
         * }
         */
    }


    @GetMapping("/profile-me")
    public String getProfileMe(@AuthenticationPrincipal UserDetails userDetails){
        return "Welcome" + userDetails.getUsername(); //mrbomberboy12345@gmail.com
    }

    @GetMapping("/oauth-success")
    public ResponseEntity<JwtResponse> oauthSuccess(@RequestParam String token) {
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
