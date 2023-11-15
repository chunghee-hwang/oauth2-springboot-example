package com.chung.oauth.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Entity
@Table(name = "managed_users", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "provider"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String email;

    @Column
    private String name;

    // oauth 제공자 (GitHub, google,...)
    @Column
    private String provider;
}
