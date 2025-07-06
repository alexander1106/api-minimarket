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
    private Double saldoSistema; 
    private String observaciones; 
    private Integer estado=1; 
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
   
    public Double getSaldoSistema() {
        return saldoSistema;
    }
    public void setSaldoSistema(Double saldoSistema) {
        this.saldoSistema = saldoSistema;
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
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    
    
}
