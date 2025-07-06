package com.gadbacorp.api.entity.ventas;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class PagosDTO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_pago")
    private Integer idPago;
    private String observaciones;
    private Date fechaPago; 
    private Double montoPagado;
    private String referenciaPago;
    private String comprobantePago;

    private String estadoPago;
    private Integer estado=1;
    private Integer id_metodo_pago;
    private Integer id_venta;
    public Integer getIdPago() {
        return idPago;
    }
    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public Date getFechaPago() {
        return fechaPago;
    }
    public void setFechaPago(Date fechaPago) {
        this.fechaPago = fechaPago;
    }
    public Double getMontoPagado() {
        return montoPagado;
    }
    public void setMontoPagado(Double montoPagado) {
        this.montoPagado = montoPagado;
    }
    public String getReferenciaPago() {
        return referenciaPago;
    }
    public void setReferenciaPago(String referenciaPago) {
        this.referenciaPago = referenciaPago;
    }
    public String getEstadoPago() {
        return estadoPago;
    }
    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public Integer getId_metodo_pago() {
        return id_metodo_pago;
    }
    public void setId_metodo_pago(Integer id_metodo_pago) {
        this.id_metodo_pago = id_metodo_pago;
    }
    public Integer getId_venta() {
        return id_venta;
    }
    public void setId_venta(Integer id_venta) {
        this.id_venta = id_venta;
    }
    public String getComprobantePago() {
        return comprobantePago;
    }
    public void setComprobantePago(String comprobantePago) {
        this.comprobantePago = comprobantePago;
    }
}
