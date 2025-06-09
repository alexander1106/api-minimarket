package com.gadbacorp.api.gadbacorp.entity.delivery;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gadbacorp.api.gadbacorp.entity.ventas.Ventas;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "Delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "direccionEntrega")
    private String direccion;

    private Integer estado;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEntrega;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEnvio;

    @Column(precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    @Column(length = 200)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "idVenta")
    @JsonIgnoreProperties("delivery")
    private Ventas venta;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
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
    
    public Ventas getVenta() {
    return venta;
    }

    public void setVenta(Ventas venta) {
    this.venta = venta;
    }

    
    // Getters y Setters
    // ...



}
