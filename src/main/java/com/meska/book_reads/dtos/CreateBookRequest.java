package com.meska.book_reads.dtos;

import lombok.Data;

@Data
public class CreateBookRequest {
    private String title;

    private String author;
}
