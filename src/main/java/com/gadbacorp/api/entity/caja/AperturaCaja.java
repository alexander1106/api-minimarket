package com.gadbacorp.api.entity.caja;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gadbacorp.api.entity.empleados.Empleado;

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
@Table(name="apertura_caja")
@SQLDelete(sql="UPDATE apertura_caja SET estado = 0 WHERE id_apertura_caja = ?")
@Where(clause = "estado = 1")
public class AperturaCaja {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idAperturaCaja; 
    private Date fechaApertura;
    private Date fechaCierre; 
    private Double saldoInicial; 
    private Double saldoFinal; 

    public AperturaCaja(Integer idAperturaCaja) {
        this.idAperturaCaja = idAperturaCaja;
    }

    public AperturaCaja() {
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idCaja")
    private Caja cajas;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idEmpleado")
    private Empleado empleados;

    @OneToMany(mappedBy = "aperturaCaja", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TransaccionesCaja> transaccionesCajas;
    
      @OneToMany(mappedBy = "aperturaCaja", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ArqueoCaja> arqueoCaja;
    

    public Caja getCajas() {
        return cajas;
    }

    public void setCajas(Caja cajas) {
        this.cajas = cajas;
    }

    public Empleado getEmpleados() {
        return empleados;
    }

    public void setEmpleados(Empleado empleados) {
        this.empleados = empleados;
    }

    public List<TransaccionesCaja> getTransaccionesCajas() {
        return transaccionesCajas;
    }

    public void setTransaccionesCajas(List<TransaccionesCaja> transaccionesCajas) {
        this.transaccionesCajas = transaccionesCajas;
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

    public Caja getCaja() {
        return cajas;
    }

    public void setCaja(Caja cajas) {
        this.cajas = cajas;
    }

    public Empleado getEmpleado() {
        return empleados;
    }

    public void setEmpleado(Empleado empleados) {
        this.empleados = empleados;
    }

    public List<ArqueoCaja> getArqueoCaja() {
        return arqueoCaja;
    }

    public void setArqueoCaja(List<ArqueoCaja> arqueoCaja) {
        this.arqueoCaja = arqueoCaja;
    }


}
