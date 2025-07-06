package com.gadbacorp.api.entity.ventas;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


public class DetallesDevolucionDTO {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idDetallesDevoluciones; 
    private Double pecioUnitario;
    private Integer cantidad;
    private Double subTotal;  
    private Integer estado=1; 
    private Integer id_producto; 
    private Integer id_devolucion;
    public Integer getIdDetallesDevoluciones() {
        return idDetallesDevoluciones;
    }
    public void setIdDetallesDevoluciones(Integer idDetallesDevoluciones) {
        this.idDetallesDevoluciones = idDetallesDevoluciones;
    }
    public Double getPecioUnitario() {
        return pecioUnitario;
    }
    public void setPecioUnitario(Double pecioUnitario) {
        this.pecioUnitario = pecioUnitario;
    }

    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public Double getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(Double subTotal) {
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
    public Integer getId_devolucion() {
        return id_devolucion;
    }
    public void setId_devolucion(Integer id_devolucion) {
        this.id_devolucion = id_devolucion;
    } 
}
