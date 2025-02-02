package com.meska.book_reads.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL; // Default to LOCAL
    private String providerId; // For OAuth user ID

    private String name; // For OAuth user's name
}

