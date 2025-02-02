package com.meska.book_reads.service;

import com.meska.book_reads.dtos.SignUpRequest;
import com.meska.book_reads.dtos.UserResponse;
import com.meska.book_reads.entity.User;
import com.meska.book_reads.exceptions.UserAlreadyExistsException;
import com.meska.book_reads.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(SignUpRequest signUpRequest){
        // checking if user already exists
        if(userRepo.existsByEmail(signUpRequest.getEmail())){
            throw new UserAlreadyExistsException("Email already exists");
        }

        // persisting user with hashed password to the database
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user = userRepo.save(user);
        return new UserResponse(user.getId(), user.getEmail());
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { // return UserDetails
        com.meska.book_reads.entity.User user = userRepo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        log.info("Inside UserService", user);
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
