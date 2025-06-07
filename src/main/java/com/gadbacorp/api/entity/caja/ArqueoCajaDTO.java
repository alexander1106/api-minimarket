package com.gadbacorp.api.entity.caja;

import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class ArqueoCajaDTO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idArqueoCaja;
    private Date fechaArqueo;
    private Double saldoInciial; 
    private Double ingresosTotales; 
    private Double egresosTotales; 
    private Double saldoSistema; 
    private Double saldoReal; 
    private Double diferencia; 
    private String observaciones; 
    private Integer id_apertura_caja;
    public Integer getIdArqueoCaja() {
        return idArqueoCaja;
    }
    public void setIdArqueoCaja(Integer idArqueoCaja) {
        this.idArqueoCaja = idArqueoCaja;
    }
    public Date getFechaArqueo() {
        return fechaArqueo;
    }
    public void setFechaArqueo(Date fechaArqueo) {
        this.fechaArqueo = fechaArqueo;
    }
    public Double getSaldoInciial() {
        return saldoInciial;
    }
    public void setSaldoInciial(Double saldoInciial) {
        this.saldoInciial = saldoInciial;
    }
    public Double getIngresosTotales() {
        return ingresosTotales;
    }
    public void setIngresosTotales(Double ingresosTotales) {
        this.ingresosTotales = ingresosTotales;
    }
    public Double getEgresosTotales() {
        return egresosTotales;
    }
    public void setEgresosTotales(Double egresosTotales) {
        this.egresosTotales = egresosTotales;
    }
    public Double getSaldoSistema() {
        return saldoSistema;
    }
    public void setSaldoSistema(Double saldoSistema) {
        this.saldoSistema = saldoSistema;
    }
    public Double getSaldoReal() {
        return saldoReal;
    }
    public void setSaldoReal(Double saldoReal) {
        this.saldoReal = saldoReal;
    }
    public Double getDiferencia() {
        return diferencia;
    }
    public void setDiferencia(Double diferencia) {
        this.diferencia = diferencia;
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
