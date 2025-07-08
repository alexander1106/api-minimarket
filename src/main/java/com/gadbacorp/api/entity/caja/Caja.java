package com.gadbacorp.api.entity.caja;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gadbacorp.api.entity.administrable.Sucursales;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private Integer estado=1;
    private String estadoCaja;

    @OneToMany(mappedBy = "caja")
    private List<AperturaCaja> aperturaCajas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idSucursal")
        private Sucursales sucursales;
    
public Caja() {
    }

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
  
    public Caja(Integer idCaja) {
        this.idCaja = idCaja;
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
    public Sucursales getSucursales() {
        return sucursales;
    }
    public void setSucursales(Sucursales sucursales) {
        this.sucursales = sucursales;
    }
    
}
