package com.gadbacorp.api.entity.empleados;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class UsuariosDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    private Integer idSucursal;
    private String nombre;
    private String apellidos;
    private String correoElectronico;
    private String contrasenaHash;
    private Integer estado;
    private Integer rollId;
    private LocalDateTime fechaCreacion;

    private String turno;
  
    public UsuariosDTO() {
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

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

      public void setContrasenaHash(String contrasenaHash) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.contrasenaHash = encoder.encode(contrasenaHash);
        this.contrasenaHash = contrasenaHash;
    }


    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getRollId() {
        return rollId;
    }

    public void setRollId(Integer rollId) {
        this.rollId = rollId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

  
    public String getTurno() {
        return turno;
    }
    public void setTurno(String turno) {
        this.turno = turno;
    }




    public Integer getIdUsuario() {
        return idUsuario;
    }




    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

   
}
