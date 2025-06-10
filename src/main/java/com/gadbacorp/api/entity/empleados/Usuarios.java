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
    private String apellidos;
    private String correoElectronico;
    private String dni;
    private String contrasenaHash;

    private Integer estado;
    private Integer rollId;
    private LocalDateTime fechaCreacion;
    private String turno;
    
    
    @Override
    public String toString() {
        return "Empleado [idUsuario=" + idUsuario + ", nombre=" + nombre + ", apellidos=" + apellidos
                + ", correoElectronico=" + correoElectronico + ", dni=" + dni + ", contrasenaHash=" + contrasenaHash
                + ", estado=" + estado + ", rollId=" + rollId + ", fechaCreacion=" + fechaCreacion  + ", turno=" + turno + ", aperturaCaja=" + aperturaCaja + "]";
    }

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

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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

}
