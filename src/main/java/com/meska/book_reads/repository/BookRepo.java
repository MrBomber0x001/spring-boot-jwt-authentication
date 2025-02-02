package com.meska.book_reads.repository;

import com.meska.book_reads.entity.Book;
import com.meska.book_reads.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepo extends JpaRepository<Book, Long> {
    List<Book> findByUser(User user);
    Optional<Book> findByIdAndUser(Long id, User user);
}
