package com.gadbacorp.api.gadbacorp.entity.dto.security;

public class Authentication2FAResponse {
    private boolean requires2fa;
    private String message;
    private String username;
    public boolean isRequires2fa() {
        return requires2fa;
    }
    public void setRequires2fa(boolean requires2fa) {
        this.requires2fa = requires2fa;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Authentication2FAResponse(boolean requires2fa, String message,String username) {
        this.requires2fa = requires2fa;
        this.message = message;
        this.username = username;


    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // constructor, getters, setters

}
