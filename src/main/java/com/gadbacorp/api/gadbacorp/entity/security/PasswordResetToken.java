package com.gadbacorp.api.gadbacorp.entity.security;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne
    private User user;

    private LocalDateTime expiration;

    private boolean used;

    public PasswordResetToken() {}

    public PasswordResetToken(String token, User user, LocalDateTime expiration, boolean used) {
        this.token = token;
        this.user = user;
        this.expiration = expiration;
        this.used = used;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    // Getters y Setters...
}
