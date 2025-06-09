package com.gadbacorp.api.entity.administrable;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresas")
@SQLDelete(sql = "UPDATE empresas SET estado=0 WHERE id = ?")
@Where(clause = "estado=1")
public class Empresas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idempresa;

    private String razonsocial;
    private String ciudad;
    private String direccion;
    private Integer cant_sucursales;
    private Integer cant_trabajadores;
    private Integer limit_inventario;
    private String correo;
    private Blob logo;
    private Integer estado = 1;

    // NUEVO: Relación con sucursales
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Sucursales> sucursales = new ArrayList<>();

    // Getters y Setters
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

    public Blob getLogo() {
        return logo;
    }

    public void setLogo(Blob logo) {
        this.logo = logo;
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

    @Override
    public String toString() {
        return "Empresas [idempresa=" + idempresa + ", razonsocial=" + razonsocial + ", ciudad=" + ciudad
                + ", direccion=" + direccion + ", cant_sucursales=" + cant_sucursales + ", cant_trabajadores="
                + cant_trabajadores + ", limit_inventario=" + limit_inventario + ", correo=" + correo + ", logo=" + logo
                + ", estado=" + estado + "]";
    }

}
