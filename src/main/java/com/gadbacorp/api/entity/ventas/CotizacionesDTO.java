package com.gadbacorp.api.entity.ventas;

import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class CotizacionesDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCotizaciones;
    private String numeroCotizacion;
    private String estadoCotizacion;
    private Date fechaCotizacion;
    private Integer estado=1;
    private Integer id_cliente;
    private double totalCotizacion;

    public Integer getIdCotizaciones() {
        return idCotizaciones;
    }
    public void setIdCotizaciones(Integer idCotizaciones) {
        this.idCotizaciones = idCotizaciones;
    }
    public String getNumeroCotizacion() {
        return numeroCotizacion;
    }
    public void setNumeroCotizacion(String numeroCotizacion) {
        this.numeroCotizacion = numeroCotizacion;
    }
    public String getEstadoCotizacion() {
        return estadoCotizacion;
    }
    public void setEstadoCotizacion(String estadoCotizacion) {
        this.estadoCotizacion = estadoCotizacion;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public Integer getId_cliente() {
        return id_cliente;
    }
    public void setId_cliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }
    public Date getFechaCotizacion() {
        return fechaCotizacion;
    }
    public void setFechaCotizacion(Date fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }
    public CotizacionesDTO(Integer idCotizaciones) {
        this.idCotizaciones = idCotizaciones;
    }
    public CotizacionesDTO() {
    }
    public double getTotalCotizacion() {
        return totalCotizacion;
    }
    public void setTotalCotizacion(double totalCotizacion) {
        this.totalCotizacion = totalCotizacion;
    }
    
}
