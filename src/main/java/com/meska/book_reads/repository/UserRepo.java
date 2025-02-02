package com.meska.book_reads.repository;

import com.meska.book_reads.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepo extends JpaRepository <User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
