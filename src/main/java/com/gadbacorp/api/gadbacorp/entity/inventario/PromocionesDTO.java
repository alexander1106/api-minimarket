package com.gadbacorp.api.gadbacorp.entity.inventario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PromocionesDTO {
    private Integer idpromocion;
    private String nombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String tipoDescuento;
    private BigDecimal valor;
    private List<Integer> productos;
    private List<Integer> categorias;

    public PromocionesDTO() { }

    public Integer getIdpromocion() {
        return idpromocion;
    }

    public void setIdpromocion(Integer idpromocion) {
        this.idpromocion = idpromocion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<Integer> getProductos() {
        return productos;
    }

    public void setProductos(List<Integer> productos) {
        this.productos = productos;
    }

    public List<Integer> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Integer> categorias) {
        this.categorias = categorias;
    }

    @Override
    public String toString() {
        return "PromocionDTO [idpromocion=" + idpromocion +
               ", nombre=" + nombre +
               ", fechaInicio=" + fechaInicio +
               ", fechaFin=" + fechaFin +
               ", tipoDescuento=" + tipoDescuento +
               ", valor=" + valor +
               ", productos=" + productos +
               ", categorias=" + categorias + "]";
    }
}

