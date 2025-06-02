package com.gadbacorp.api.entity.ventas;

import java.util.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="devoluciones")
@SQLDelete(sql="UPDATE devoluciones SET estado = 0 WHERE id_devolucion = ?")
@Where(clause = "estado = 1")
public class Devoluciones {
@   Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id_devolucion;
    private Date fechaDevolucion;
    private String motivoDevolucion;
    private Double montoDevuelto ;
    private String estadoDevolucion ;
    private String observaciones ;
    private Integer estado=1;

    
    @ManyToOne
    @JoinColumn(name = "idVenta")
    @JsonIgnore
    private Ventas ventas;

    public Integer getId_devolucion() {
        return id_devolucion;
    }

    public void setId_devolucion(Integer id_devolucion) {
        this.id_devolucion = id_devolucion;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public String getMotivoDevolucion() {
        return motivoDevolucion;
    }

    public void setMotivoDevolucion(String motivoDevolucion) {
        this.motivoDevolucion = motivoDevolucion;
    }

    public Double getMontoDevuelto() {
        return montoDevuelto;
    }

    public void setMontoDevuelto(Double montoDevuelto) {
        this.montoDevuelto = montoDevuelto;
    }

    public String getEstadoDevolucion() {
        return estadoDevolucion;
    }

    public void setEstadoDevolucion(String estadoDevolucion) {
        this.estadoDevolucion = estadoDevolucion;
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

    public Ventas getVentas() {
        return ventas;
    }

    public void setVentas(Ventas ventas) {
        this.ventas = ventas;
    }
}
