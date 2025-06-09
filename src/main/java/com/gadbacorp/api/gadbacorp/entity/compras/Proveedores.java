package com.gadbacorp.api.gadbacorp.entity.compras;

import java.sql.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="proveedores")
@SQLDelete(sql = "UPDATE proveedores SET estado=0 WHERE id = ?")
@Where(clause = "estado=1")

public class Proveedores {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id_Proveedor;
    private String nombre;
    private String ruc;
    private String regimen;
    private String telefono;
    private String gmail;
    private String direccion;
    private Date fecha_registro;
    private Integer estado = 1;
    public Integer getId_Proveedor() {
        return Id_Proveedor;
    }
    public void setId_Proveedor(Integer id_Proveedor) {
        Id_Proveedor = id_Proveedor;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getRuc() {
        return ruc;
    }
    public void setRuc(String ruc) {
        this.ruc = ruc;
    }
    public String getRegimen() {
        return regimen;
    }
    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getGmail() {
        return gmail;
    }
    public void setGmail(String gmail) {
        this.gmail = gmail;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public Date getFecha_registro() {
        return fecha_registro;
    }
    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    @Override
    public String toString() {
        return "Proveedores [Id_Proveedor=" + Id_Proveedor + ", nombre=" + nombre + ", ruc=" + ruc + ", regimen="
                + regimen + ", telefono=" + telefono + ", gmail=" + gmail + ", direccion=" + direccion
                + ", fecha_registro=" + fecha_registro + ", estado=" + estado + "]";
    }

}
