package com.gadbacorp.api.entity.empleados;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.gadbacorp.api.entity.administrable.Sucursales;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Empleados")
@SQLDelete(sql = "UPDATE Empleados SET estado = 0 WHERE idEmpleado = ?")
@Where(clause = "estado = 1")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEmpleado;
    @ManyToOne
    @JoinColumn(name = "idSucursal")
    private Sucursales idSucursal;
    
    private String nombre;
    private String apellidos;
    private String correoElectronico;
    private String dni;
    private String contrasenaHash;
    private Integer estado;
    private Integer rollId;
    private LocalDateTime fechaCreacion;
    private Integer permisosId;
    private String turno;
    public Empleado() {
    }

    public Empleado(Integer idEmpleado, Sucursales idSucursal, String nombre, String apellidos, String correoElectronico,
            String dni, String contrasenaHash, Integer estado, Integer rollId, LocalDateTime fechaCreacion,
            Integer permisosId, String turno) {
        this.idEmpleado = idEmpleado;
        this.idSucursal = idSucursal;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correoElectronico = correoElectronico;
        this.dni = dni;
        this.contrasenaHash = contrasenaHash;
        this.estado = estado;
        this.rollId = rollId;
        this.fechaCreacion = fechaCreacion;
        this.permisosId = permisosId;
        this.turno = turno;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Sucursales getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Sucursales idSucursal) {
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

    public Integer getPermisosId() {
        return permisosId;
    }

    public void setPermisosId(Integer permisosId) {
        this.permisosId = permisosId;
    }
    public String getTurno() {
        return turno;
    }
    public void setTurno(String turno) {
        this.turno = turno;
    }

    @Override
    public String toString() {
        return "empleado [idEmpleado=" + idEmpleado + ", idSucursal=" + idSucursal + ", nombre=" + nombre
                + ", apellidos=" + apellidos + ", correoElectronico=" + correoElectronico + ", dni=" + dni
                + ", contrasenaHash=" + contrasenaHash + ", estado=" + estado + ", rollId=" + rollId
                + ", fechaCreacion=" + fechaCreacion + ", permisosId=" + permisosId + ", turno=" + turno + "]";
    }
    

}
