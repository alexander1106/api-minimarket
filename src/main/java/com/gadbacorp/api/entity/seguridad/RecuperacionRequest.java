package com.gadbacorp.api.entity.seguridad;

/** Cuerpo de POST /enviar-link-recuperacion: {"email": "..."} */
public class RecuperacionRequest {

    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
