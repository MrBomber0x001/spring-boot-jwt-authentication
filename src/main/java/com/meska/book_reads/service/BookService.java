package com.meska.book_reads.service;

import com.meska.book_reads.dtos.BookResponse;
import com.meska.book_reads.dtos.CreateBookRequest;
import com.meska.book_reads.entity.Book;
import com.meska.book_reads.entity.User;
import com.meska.book_reads.repository.BookRepo;
import com.meska.book_reads.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepo bookRepo;
    private final UserRepo userRepo;

    public BookResponse createBook(CreateBookRequest bookRequest, String userEmail){
        // user existance check
        User user = userRepo.findByEmail(userEmail).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );

        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setUser(user);

        book = bookRepo.save(book);
        return new BookResponse(book.getId(), book.getTitle(), book.getAuthor());
    }
}
