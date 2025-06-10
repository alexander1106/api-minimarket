package com.gadbacorp.api.entity.delivery;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gadbacorp.api.entity.ventas.Ventas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "delivery")
@SQLDelete(sql = "UPDATE delivery SET estado = 0 WHERE iddelivery = ?")
@Where(clause = "estado = 1")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer iddelivery;
    private String direccion;
    private Date fechaEnvio;
    private Date fechaEntrega;
    private String encargado;
    private BigDecimal costoEnvio;
    private String observaciones;
    private Integer estado = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idventa")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ventas venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idvehiculo")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Vehiculo vehiculo;

    public Delivery() {
    }

    public Integer getIddelivery() {
        return iddelivery;
    }

    public void setIddelivery(Integer iddelivery) {
        this.iddelivery = iddelivery;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Ventas getVenta() {
        return venta;
    }

    public void setVenta(Ventas venta) {
        this.venta = venta;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    @Override
    public String toString() {
        return "Delivery [iddelivery=" + iddelivery + ", direccion=" + direccion + ", fechaEnvio=" + fechaEnvio
                + ", fechaEntrega=" + fechaEntrega + ", encargado=" + encargado + ", costoEnvio=" + costoEnvio
                + ", observaciones=" + observaciones + ", estado=" + estado + ", venta=" + venta + ", vehiculo="
                + vehiculo + "]";
    }

}