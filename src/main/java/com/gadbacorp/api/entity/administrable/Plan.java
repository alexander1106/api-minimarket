package com.gadbacorp.api.entity.administrable;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Plan de suscripcion de la plataforma (Gratis, Basico, Profesional, Empresarial). Los limites nulos significan "sin limite". */
@Entity
@Table(name = "planes")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPlan;

    @Column(unique = true, length = 60)
    private String nombre;

    @Column(precision = 12, scale = 2)
    private BigDecimal precioMensual = BigDecimal.ZERO;

    private Integer maxUsuarios;
    private Integer maxSucursales;
    private Integer estado = 1;

    public Plan() {
    }

    public Plan(String nombre, BigDecimal precioMensual, Integer maxUsuarios, Integer maxSucursales) {
        this.nombre = nombre;
        this.precioMensual = precioMensual;
        this.maxUsuarios = maxUsuarios;
        this.maxSucursales = maxSucursales;
    }

    public Integer getIdPlan() {
        return idPlan;
    }

    public void setIdPlan(Integer idPlan) {
        this.idPlan = idPlan;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioMensual() {
        return precioMensual;
    }

    public void setPrecioMensual(BigDecimal precioMensual) {
        this.precioMensual = precioMensual;
    }

    public Integer getMaxUsuarios() {
        return maxUsuarios;
    }

    public void setMaxUsuarios(Integer maxUsuarios) {
        this.maxUsuarios = maxUsuarios;
    }

    public Integer getMaxSucursales() {
        return maxSucursales;
    }

    public void setMaxSucursales(Integer maxSucursales) {
        this.maxSucursales = maxSucursales;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}
