package com.gadbacorp.api.entity.caja;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "cajas")
@SQLDelete(sql="UPDATE cajas SET estado = 0 WHERE id_caja = ?")
@Where(clause = "estado = 1")
public class Caja {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idCaja; 
    private String nombreCaja;
    private Double saldoActual; 
    private Integer estado;
    private String estadoCaja;

    @OneToMany(mappedBy = "cajas", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<AperturaCaja> aperturaCajas;

    
    @OneToMany(mappedBy = "cajaOrigen", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TransferenciasEntreCajas> transferenciasOrigen;

    @OneToMany(mappedBy = "cajaDestino", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TransferenciasEntreCajas> transferenciasDestino;
    
    
    public List<TransferenciasEntreCajas> getTransferenciasOrigen() {
        return transferenciasOrigen;
    }
    public void setTransferenciasOrigen(List<TransferenciasEntreCajas> transferenciasOrigen) {
        this.transferenciasOrigen = transferenciasOrigen;
    }
    public List<TransferenciasEntreCajas> getTransferenciasDestino() {
        return transferenciasDestino;
    }
    public void setTransferenciasDestino(List<TransferenciasEntreCajas> transferenciasDestino) {
        this.transferenciasDestino = transferenciasDestino;
    }
    public Caja(Integer id_caja) {
        //TODO Auto-generated constructor stub
    }
    public Integer getIdCaja() {
        return idCaja;
    }
    public void setIdCaja(Integer idCaja) {
        this.idCaja = idCaja;
    }
    public String getNombreCaja() {
        return nombreCaja;
    }
    public void setNombreCaja(String nombreCaja) {
        this.nombreCaja = nombreCaja;
    }
    public Double getSaldoActual() {
        return saldoActual;
    }
    public void setSaldoActual(Double saldoActual) {
        this.saldoActual = saldoActual;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public String getEstadoCaja() {
        return estadoCaja;
    }
    public void setEstadoCaja(String estadoCaja) {
        this.estadoCaja = estadoCaja;
    }
    public List<AperturaCaja> getAperturaCajas() {
        return aperturaCajas;
    }
    public void setAperturaCajas(List<AperturaCaja> aperturaCajas) {
        this.aperturaCajas = aperturaCajas;
    }
    

    
}
