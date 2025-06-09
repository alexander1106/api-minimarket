package com.gadbacorp.api.entity.ventas;

import java.time.LocalDate;
import jakarta.persistence.Id;
public class DetallesCotizacionesDTO {
    @Id
    private Integer idDetallesCotizaciones;
    private LocalDate fechaCotizaciones;
    private Integer cantidad;
    private double subTotal;  
    private Integer estado=1;
    private Integer id_producto;
    private Integer id_cotizacion;
    private Integer precioUnitario;

    public Integer getIdDetallesCotizaciones() {
        return idDetallesCotizaciones;
    }
    public void setIdDetallesCotizaciones(Integer idDetallesCotizaciones) {
        this.idDetallesCotizaciones = idDetallesCotizaciones;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public double getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public Integer getId_producto() {
        return id_producto;
    }
    public void setId_producto(Integer id_producto) {
        this.id_producto = id_producto;
    }
    public Integer getId_cotizacion() {
        return id_cotizacion;
    }
    public void setId_cotizacion(Integer id_cotizacion) {
        this.id_cotizacion = id_cotizacion;
    }
    public LocalDate getFechaCotizaciones() {
        return fechaCotizaciones;
    }
    public void setFechaCotizaciones(LocalDate fechaCotizaciones) {
        this.fechaCotizaciones = fechaCotizaciones;
    }
    public Integer getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(Integer precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
