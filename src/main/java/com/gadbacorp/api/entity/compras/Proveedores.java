package com.gadbacorp.api.entity.compras;

import java.sql.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gadbacorp.api.entity.administrable.Empresas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveedores")
@SQLDelete(sql = "UPDATE proveedores SET estado=0 WHERE Id_Proveedor = ?")
@Where(clause = "estado=1")
public class Proveedores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Proveedor")
    private Integer idProveedor;

    private String nombre;
    private String ruc;
    private String regimen;
    private String telefono;
    private String gmail;
    private String direccion;
    private Date fecha_registro;
    private Integer estado = 1;

    @ManyToOne
    @JoinColumn(name = "id_empresa", referencedColumnName = "idempresa")
    @JsonBackReference
    private Empresas empresa;

    // Getters y Setters
    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
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

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    @Override
    public String toString() {
        return "Proveedores [idProveedor=" + idProveedor + ", nombre=" + nombre + ", ruc=" + ruc + ", regimen="
                + regimen + ", telefono=" + telefono + ", gmail=" + gmail + ", direccion=" + direccion
                + ", fecha_registro=" + fecha_registro + ", estado=" + estado
                + ", empresa=" + (empresa != null ? empresa.getIdempresa() : "Sin Empresa") + "]";
    }
}
