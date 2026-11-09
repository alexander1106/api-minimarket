package com.gadbacorp.api.entity.seguridad;

public class ResetPasswordRequest {

    private String token;
    @com.fasterxml.jackson.annotation.JsonAlias({"nuevaClave", "nuevaPassword"})
    private String newPassword;

    public String getToken() {
        return token;
    }

    public ResetPasswordRequest() {
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
