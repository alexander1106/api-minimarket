package com.gadbacorp.api.entity.ventas;

import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class CotizacionesDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCotizaciones;
    private String numeroCotizacion;
    private String estadoCotizacion;
    private LocalDate fechaCotizacion;
        private LocalDate fechaVencimiento;

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
    public LocalDate getFechaCotizacion() {
        return fechaCotizacion;
    }
    public void setFechaCotizacion(LocalDate fechaCotizacion) {
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
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
    
}
