package com.gadbacorp.api.entity.compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DevolucionesCompraDTO {
    private Integer idCompra;
    private BigDecimal montoDerivativo;
    private String motivoDevolucion;
    private String observaciones;
    private List<DetalleDevolucionCompraDTO> detalles;

    // Getters y Setters
    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public BigDecimal getMontoDerivativo() {
        return montoDerivativo;
    }

    public void setMontoDerivativo(BigDecimal montoDerivativo) {
        this.montoDerivativo = montoDerivativo;
    }

    public String getMotivoDevolucion() {
        return motivoDevolucion;
    }

    public void setMotivoDevolucion(String motivoDevolucion) {
        this.motivoDevolucion = motivoDevolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<DetalleDevolucionCompraDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleDevolucionCompraDTO> detalles) {
        this.detalles = detalles;
    }
}