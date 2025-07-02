package com.gadbacorp.api.entity.caja;

import java.util.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.gadbacorp.api.entity.ventas.MetodosPago;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="transacciones_caja")
@SQLDelete(sql="UPDATE transacciones_caja SET estado = 0 WHERE id_transacciones_caja = ?")
@Where(clause = "estado = 1")
public class TransaccionesCaja {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idTransaccionesCaja;
    private String tipoMovimiento; 
    private Double monto; 
    private Integer estado=1;
    private Date fecha; 
    private String observaciones; 

 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idAperturaCaja")
    private AperturaCaja aperturaCaja;
     // Aquí agregas la relación al método de pago
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idMetodoPago")
    private MetodosPago metodoPago;

    

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

    public AperturaCaja getAperturaCaja() {
        return aperturaCaja;
    }

    public void setAperturaCaja(AperturaCaja aperturaCaja) {
        this.aperturaCaja = aperturaCaja;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public MetodosPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodosPago metodoPago) {
        this.metodoPago = metodoPago;
    }

  
}
