package com.gadbacorp.api.entity.ventas;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class DetallesVentasDTO {   
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idDetallesVenta;

    private double pecioUnitario;
    private String fechaVenta;
    private Integer cantidad;
    private double subTotal;      
    private Integer estado=1;
    private Integer id_producto;
    private Integer id_venta;

     public DetallesVentasDTO() {
    }
     public DetallesVentasDTO(Integer idDetallesVenta) {
        this.idDetallesVenta = idDetallesVenta;
    }
    public Integer getIdDetallesVenta() {
        return idDetallesVenta;
    }
    public void setIdDetallesVenta(Integer idDetallesVenta) {
        this.idDetallesVenta = idDetallesVenta;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }
    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
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
    public Integer getId_venta() {
        return id_venta;
    }
    public void setId_venta(Integer id_venta) {
        this.id_venta = id_venta;
    }
 
    public double getPecioUnitario() {
        return pecioUnitario;
    }
    public void setPecioUnitario(double pecioUnitario) {
        this.pecioUnitario = pecioUnitario;
    }
}
