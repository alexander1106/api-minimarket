package com.gadbacorp.api.entity.empleados;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.entity.seguridad.Rol;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
@SQLDelete(sql = "UPDATE usuarios SET estado = 0 WHERE id_usuario = ?")
@Where(clause = "estado = 1")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    private String nombre;
    private String username;
    private String apellidos;
    private String email;
    private String dni;
    private String password;
    private Integer estado=1;
    private LocalDateTime fechaCreacion;
    private String turno;
    @ManyToOne
    @JoinColumn(name = "rol_id", referencedColumnName = "id") // 'id' es el PK de Rol
    private Rol rol;

    
    public Usuarios(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuarios( ) {
    }

    @OneToMany(mappedBy = "usuarios", cascade = CascadeType.ALL)
     @JsonIgnore
    private List<AperturaCaja> aperturaCaja;

    @ManyToOne
@JoinColumn(name = "id_sucursal", referencedColumnName = "id_sucursal")
private Sucursales sucursal;
 
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

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }


    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
 BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
        this.password = password;    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
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

    public List<AperturaCaja> getAperturaCaja() {
        return aperturaCaja;
    }


    public void setAperturaCaja(List<AperturaCaja> aperturaCaja) {
        this.aperturaCaja = aperturaCaja;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
    

    public Sucursales getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

}
