package com.gadbacorp.api.entity.inventario;

import java.time.LocalDate;

public class AlmacenProductosDTO {
    private Integer idalmacenproducto;
    private Integer stock;
    private LocalDate fechaIngreso;
    private Integer estado;
    private Integer idproducto;
    private Integer idAlmacen;

    public AlmacenProductosDTO() { }

    public AlmacenProductosDTO(Integer idalmacenproducto, Integer stock, LocalDate fechaIngreso, Integer estado,
                              Integer idProducto, Integer idAlmacen) {
        this.idalmacenproducto = idalmacenproducto;
        this.stock = stock;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
        this.idproducto = idProducto;
        this.idAlmacen = idAlmacen;
    }

    public Integer getIdalmacenproducto() {
        return idalmacenproducto;
    }

    public void setIdalmacenproducto(Integer idalmacenproducto) {
        this.idalmacenproducto = idalmacenproducto;
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

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getIdProducto() {
        return idproducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idproducto = idProducto;
    }

    public Integer getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }

    public Integer getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(Integer idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    @Override
    public String toString() {
        return "AlmacenProductoDTO{" +
               "idalmacenproducto=" + idalmacenproducto +
               ", stock=" + stock +
               ", fechaIngreso=" + fechaIngreso +
               ", estado=" + estado +
               ", idProducto=" + idproducto +
               ", idAlmacen=" + idAlmacen +
               '}';
    }

}
