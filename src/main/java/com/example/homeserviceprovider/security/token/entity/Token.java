package com.example.homeserviceprovider.security.token.entity;


import com.example.homeserviceprovider.base.domain.BaseEntity;
import com.example.homeserviceprovider.domain.user.Users;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Token extends BaseEntity<Long> {

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private Users users;


    public Token( LocalDateTime createdAt, LocalDateTime expiresAt, Users users) {
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.users = users;
    }

      public Token() {

      }
}
