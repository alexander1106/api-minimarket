package com.gadbacorp.api.entity.delivery;

import java.math.BigDecimal;
import java.util.Date;

public class DeliveryDTO {
    private Integer iddelivery;
    private String direccion;
    private Date fechaEnvio;
    private Date fechaEntrega;
    private String encargado;
    private BigDecimal costoEnvio;
    private String observaciones;
    private Integer estado;
    private Integer idventa;
    private Integer idvehiculo;

    public DeliveryDTO() { }

    public DeliveryDTO(Integer iddelivery, String direccion, Date fechaEnvio, Date fechaEntrega,
                       String encargado, BigDecimal costoEnvio, String observaciones,
                       Integer estado, Integer idventa, Integer idvehiculo) {
        this.iddelivery = iddelivery;
        this.direccion = direccion;
        this.fechaEnvio = fechaEnvio;
        this.fechaEntrega = fechaEntrega;
        this.encargado = encargado;
        this.costoEnvio = costoEnvio;
        this.observaciones = observaciones;
        this.estado = estado;
        this.idventa = idventa;
        this.idvehiculo = idvehiculo;
    }

    public Integer getIddelivery() {
        return iddelivery;
    }

    public void setIddelivery(Integer iddelivery) {
        this.iddelivery = iddelivery;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
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

    public Integer getIdventa() {
        return idventa;
    }

    public void setIdventa(Integer idventa) {
        this.idventa = idventa;
    }

    public Integer getIdvehiculo() {
        return idvehiculo;
    }

    public void setIdvehiculo(Integer idvehiculo) {
        this.idvehiculo = idvehiculo;
    }

    @Override
    public String toString() {
        return "DeliveryDTO [iddelivery=" + iddelivery + ", direccion=" + direccion + ", fechaEnvio=" + fechaEnvio
                + ", fechaEntrega=" + fechaEntrega + ", encargado=" + encargado + ", costoEnvio=" + costoEnvio
                + ", observaciones=" + observaciones + ", estado=" + estado + ", idventa=" + idventa + ", idvehiculo="
                + idvehiculo + "]";
    }

    
}