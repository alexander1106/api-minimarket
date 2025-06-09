package com.gadbacorp.api.gadbacorp.entity.dto.security;

public class AuthenticationResponse {

    private String jwt;

    // Constructor
    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    // Getter para que Jackson pueda serializar el campo a JSON
    public String getJwt() {
        return jwt;
    }

    // (Opcional) Setter si necesitas modificar el valor de jwt más adelante
    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
