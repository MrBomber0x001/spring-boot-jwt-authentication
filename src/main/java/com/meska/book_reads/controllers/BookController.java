package com.meska.book_reads.controllers;

import com.meska.book_reads.dtos.BookResponse;
import com.meska.book_reads.dtos.CreateBookRequest;
import com.meska.book_reads.service.BookService;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {
    private static final Logger log = LogManager.getLogger(BookController.class);
    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody CreateBookRequest bookRequest,
                                                   Authentication authentication) {
        String userEmail = authentication.getName();
        log.info("inside BookController - userEmail", userEmail);
        return ResponseEntity.ok(bookService.createBook(bookRequest, userEmail));
    }
}
