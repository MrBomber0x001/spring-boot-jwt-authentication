package com.meska.book_reads.dtos;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
public class SignUpRequest {
    private String email;

    private String password;
}
