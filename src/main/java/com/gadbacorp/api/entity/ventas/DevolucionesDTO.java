package com.gadbacorp.api.entity.ventas;

import java.time.LocalDate;
import java.util.Date;

public class DevolucionesDTO {
    private Integer id_devolucion;
    private LocalDate fechaDevolucion;
    private String motivoDevolucion;
    private Double montoDevuelto ;
    private String observaciones ;
    private Integer estado=1;
    private Integer id_venta;
    public Integer getId_devolucion() {
        return id_devolucion;
    }
    public void setId_devolucion(Integer id_devolucion) {
        this.id_devolucion = id_devolucion;
    }
    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }
    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
    public String getMotivoDevolucion() {
        return motivoDevolucion;
    }
    public void setMotivoDevolucion(String motivoDevolucion) {
        this.motivoDevolucion = motivoDevolucion;
    }
    public Double getMontoDevuelto() {
        return montoDevuelto;
    }
    public void setMontoDevuelto(Double montoDevuelto) {
        this.montoDevuelto = montoDevuelto;
    }

    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public Integer getId_venta() {
        return id_venta;
    }
    public void setId_venta(Integer id_venta) {
        this.id_venta = id_venta;
    } 
}
