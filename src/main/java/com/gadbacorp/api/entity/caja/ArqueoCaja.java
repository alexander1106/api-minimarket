package com.gadbacorp.api.entity.caja;

import java.util.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "arqueo_caja")
@SQLDelete(sql="UPDATE arqueo_caja SET estado = 0 WHERE id_arqueo_caja = ?")
@Where(clause = "estado = 1")
public class ArqueoCaja {
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


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idAperturaCaja")
    private AperturaCaja aperturaCaja;

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

    public AperturaCaja getAperturaCaja() {
        return aperturaCaja;
    }

    public void setAperturaCaja(AperturaCaja aperturaCaja) {
        this.aperturaCaja = aperturaCaja;
    }
}
