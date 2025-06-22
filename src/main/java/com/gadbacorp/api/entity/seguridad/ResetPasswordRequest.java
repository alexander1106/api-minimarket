package com.gadbacorp.api.entity.seguridad;

public class ResetPasswordRequest {

    private String token;
    private String newPassword;

    public String getToken() {
        return token;
    }

    public ResetPasswordRequest(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
