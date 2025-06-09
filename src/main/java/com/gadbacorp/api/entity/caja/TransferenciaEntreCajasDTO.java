package com.gadbacorp.api.entity.caja;

import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class TransferenciaEntreCajasDTO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idTransferenciaEntreCajas; 
    private Double monto;
    private Date fecha;
    private String motivo; 
    private String observaciones; 
    private Integer id_caja_origen; 
    private Integer id_caja_destino;
    public Integer getIdTransferenciaEntreCajas() {
        return idTransferenciaEntreCajas;
    }
    public void setIdTransferenciaEntreCajas(Integer idTransferenciaEntreCajas) {
        this.idTransferenciaEntreCajas = idTransferenciaEntreCajas;
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
    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public Integer getId_caja_origen() {
        return id_caja_origen;
    }
    public void setId_caja_origen(Integer id_caja_origen) {
        this.id_caja_origen = id_caja_origen;
    }
    public Integer getId_caja_destino() {
        return id_caja_destino;
    }
    public void setId_caja_destino(Integer id_caja_destino) {
        this.id_caja_destino = id_caja_destino;
    }
    
}
