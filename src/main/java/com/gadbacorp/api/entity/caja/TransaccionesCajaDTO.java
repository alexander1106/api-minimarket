package com.gadbacorp.api.entity.caja;

import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class TransaccionesCajaDTO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idTransaccionesCaja;
    private String tipoMovimiento; 
    private String concepto; 
    private Double monto; 
    private Date fecha; 
    private String observaciones; 
    private Integer id_apertura_caja;
    public Integer getIdTransaccionesCaja() {
        return idTransaccionesCaja;
    }
    public void setIdTransaccionesCaja(Integer idTransaccionesCaja) {
        this.idTransaccionesCaja = idTransaccionesCaja;
    }
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }
    public String getConcepto() {
        return concepto;
    }
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
    public Double getMonto() {
        return monto;
    }
    public void setMonto(Double monto) {
        this.monto = monto;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public Integer getId_apertura_caja() {
        return id_apertura_caja;
    }
    public void setId_apertura_caja(Integer id_apertura_caja) {
        this.id_apertura_caja = id_apertura_caja;
    } 
    
}
