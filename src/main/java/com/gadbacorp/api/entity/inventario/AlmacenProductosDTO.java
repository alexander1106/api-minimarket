package com.gadbacorp.api.entity.inventario;

import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;



public class AlmacenProductosDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idalmacen;
    private Integer stock;
    private LocalDate fechaIngreso;

    public AlmacenProductosDTO() { }

    public AlmacenProductosDTO(Integer idalmacen, Integer stock, LocalDate fechaIngreso) {
        this.idalmacen = idalmacen;
        this.stock = stock;
        this.fechaIngreso = fechaIngreso;
    }

    public Integer getIdalmacen() {
        return idalmacen;
    }

    public void setIdalmacen(Integer idalmacen) {
        this.idalmacen = idalmacen;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    @Override
    public String toString() {
        return "AlmacenProductosDTO [idalmacen=" + idalmacen + ", stock=" + stock + ", fechaIngreso=" + fechaIngreso
                + "]";
    }
    
}
