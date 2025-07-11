package com.gadbacorp.api.entity.empleados;

import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class UsuariosDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    private Integer idEmpresa;
    private Integer idSucursal;
    private String nombre;
    private String username; 
    private String apellidos;
    private String email;
    private String password;
    private String dni;
    private String turno; 
    private Integer estado=1;
    private Integer rolId;
    private LocalDateTime fechaCreacion;

    public UsuariosDTO() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }


    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }


    public Integer getIdSucursal() {
        return idSucursal;
    }


    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }


    public String getNombre() {
        return nombre;
    }


    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getApellidos() {
        return apellidos;
    }


    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getTurno() {
        return turno;
    }


    public void setTurno(String turno) {
        this.turno = turno;
    }


    public Integer getEstado() {
        return estado;
    }


    public void setEstado(Integer estado) {
        this.estado = estado;
    }


    public Integer getRolId() {
        return rolId;
    }


    public void setRolId(Integer rolId) {
        this.rolId = rolId;
    }


    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }


    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }


    public String getDni() {
        return dni;
    }


    public void setDni(String dni) {
        this.dni = dni;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    
}