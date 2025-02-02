package com.meska.book_reads.dtos;

import com.meska.book_reads.entity.AuthProvider;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OAuthUserInfo {
        private String email;
        private String name;
        private String providerId;
        private AuthProvider provider;
}
