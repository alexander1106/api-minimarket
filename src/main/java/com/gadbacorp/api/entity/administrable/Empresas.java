package com.gadbacorp.api.entity.administrable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gadbacorp.api.entity.compras.Proveedores;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresas")
@SQLDelete(sql = "UPDATE empresas SET estado=0 WHERE idempresa = ?")
@Where(clause = "estado=1")
public class Empresas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idempresa;
    private String razonsocial;
    private String ciudad;
    private String direccion;
    private Integer cant_sucursales;
    private String ruc;
    private LocalDate fechaRegistro;
    private Integer cant_cajas;
    private Integer cant_trabajadores;
    private Integer limit_inventario;
    private String correo;
    @Lob
    @Column(name="logo", columnDefinition="LONGTEXT")
    private String logo;
    private Integer estado = 1;
    

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sucursales> sucursales = new ArrayList<>();

    // NUEVO: Relación con proveedores
     @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonManagedReference
    private List<Proveedores> proveedores = new ArrayList<>();

    public Empresas(Integer idempresa) {
        this.idempresa = idempresa;
    }

    public Empresas() {
    }

    public Integer getIdempresa() {
        return idempresa;
    }

    public void setIdempresa(Integer idempresa) {
        this.idempresa = idempresa;
    }

    public String getRazonsocial() {
        return razonsocial;
    }

    public void setRazonsocial(String razonsocial) {
        this.razonsocial = razonsocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getCant_cajas() {
        return cant_cajas;
    }

    public void setCant_cajas(Integer cant_cajas) {
        this.cant_cajas = cant_cajas;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getCant_sucursales() {
        return cant_sucursales;
    }

    public void setCant_sucursales(Integer cant_sucursales) {
        this.cant_sucursales = cant_sucursales;
    }

    public Integer getCant_trabajadores() {
        return cant_trabajadores;
    }

    public void setCant_trabajadores(Integer cant_trabajadores) {
        this.cant_trabajadores = cant_trabajadores;
    }

    public Integer getLimit_inventario() {
        return limit_inventario;
    }

    public void setLimit_inventario(Integer limit_inventario) {
        this.limit_inventario = limit_inventario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

  
    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public List<Sucursales> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<Sucursales> sucursales) {
        this.sucursales = sucursales;
    }

    public List<Proveedores> getProveedores() {
        return proveedores;
    }

    public void setProveedores(List<Proveedores> proveedores) {
        this.proveedores = proveedores;
    }

    @Override
    public String toString() {
        return "Empresas [idempresa=" + idempresa + ", razonsocial=" + razonsocial + ", ciudad=" + ciudad
                + ", direccion=" + direccion + ", cant_sucursales=" + cant_sucursales + ", cant_trabajadores="
                + cant_trabajadores + ", limit_inventario=" + limit_inventario + ", correo=" + correo + ", logo=" + logo
                + ", estado=" + estado + "]";
    }
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }

}