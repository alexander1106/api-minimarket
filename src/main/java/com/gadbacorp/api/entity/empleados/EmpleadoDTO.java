package com.gadbacorp.api.entity.empleados;


import java.time.LocalDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class EmpleadoDTO {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEmpleado;
    private Integer idSucursal;
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
  

  

    public EmpleadoDTO() {
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
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
        return "empleado [idEmpleado=" + idEmpleado + 
                ", idSucursal=" + idSucursal + 
                ", nombre=" + nombre + 
                ", apellidos=" + apellidos + 
                ", correoElectronico=" + correoElectronico + 
                ", dni=" + dni + 
                ", contrasenaHash=" + contrasenaHash + 
                ", estado=" + estado + 
                ", rollId=" + rollId + 
                ", fechaCreacion=" + fechaCreacion + 
                ", permisosId=" + permisosId + 
                ", turno=" + turno + "]";
    }
}
