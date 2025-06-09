package com.gadbacorp.api.entity.caja;

import java.util.Date;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class AperturaCajaDTO {
     @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idAperturaCaja; 
    private Date fechaApertura;
    private Date fechaCierre; 
    private Double saldoInicial; 
    private Double saldoFinal;
    private Integer id_empleado; 
    private Integer id_caja;

    public AperturaCajaDTO(Integer idAperturaCaja) {
        this.idAperturaCaja = idAperturaCaja;
    }
    public AperturaCajaDTO() {
    }
    public Integer getIdAperturaCaja() {
        return idAperturaCaja;
    }
    public void setIdAperturaCaja(Integer idAperturaCaja) {
        this.idAperturaCaja = idAperturaCaja;
    }
    public Date getFechaApertura() {
        return fechaApertura;
    }
    public void setFechaApertura(Date fechaApertura) {
        this.fechaApertura = fechaApertura;
    }
    public Date getFechaCierre() {
        return fechaCierre;
    }
    public void setFechaCierre(Date fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
    public Double getSaldoInicial() {
        return saldoInicial;
    }
    public void setSaldoInicial(Double saldoInicial) {
        this.saldoInicial = saldoInicial;
    }
    public Double getSaldoFinal() {
        return saldoFinal;
    }
    public void setSaldoFinal(Double saldoFinal) {
        this.saldoFinal = saldoFinal;
    }
    public Integer getId_empleado() {
        return id_empleado;
    }
    public void setId_empleado(Integer id_empleado) {
        this.id_empleado = id_empleado;
    }
    public Integer getId_caja() {
        return id_caja;
    }
    public void setId_caja(Integer id_caja) {
        this.id_caja = id_caja;
    } 
     

}
