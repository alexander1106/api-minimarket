package com.gadbacorp.api.controller.seguridad;

import com.gadbacorp.api.entity.empleados.Usuarios;

public class AuthResponse {
    private String token;
    private String rol;
    private Usuarios usuario;

    public AuthResponse() {}
    public AuthResponse(String token, String rol, Usuarios usuario) {
        this.token = token;
        this.rol = rol;
        this.usuario = usuario;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    public Usuarios getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuarios usuario) {
        this.usuario = usuario;
    }
    
}