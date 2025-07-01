package com.gadbacorp.api.entity.caja;

public class CajaDTO {
    private Integer idCaja;
    private String nombreCaja;
    private Double saldoActual; 
    private Integer estado=1;
    private String estadoCaja;
    private Integer idSucursal;
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
    public Integer getIdSucursal() {
        return idSucursal;
    }
    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }
    public Integer getIdCaja() {
        return idCaja;
    }
    public void setIdCaja(Integer idCaja) {
        this.idCaja = idCaja;
    }
    

}
