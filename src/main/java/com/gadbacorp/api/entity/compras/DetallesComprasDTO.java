package com.gadbacorp.api.entity.compras;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class DetallesComprasDTO {
    private Integer idDetalleCompra;
    private Integer idCompra;
    
    @NotNull(message = "El producto es obligatorio")
    private Integer idProducto;
    private String nombreProducto; // Nuevo campo para mostrar el nombre
    
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Integer cantidad;
    
    private BigDecimal precioUnitario;
    private BigDecimal precioCompra; // Se cargará automáticamente
    private BigDecimal precioVenta; // Se cargará automáticamente
    private BigDecimal subTotal;
    private Integer estado = 1;
    public Integer getIdDetalleCompra() {
        return idDetalleCompra;
    }
    public void setIdDetalleCompra(Integer idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
    }
    public Integer getIdCompra() {
        return idCompra;
    }
    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }
    public Integer getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }
    public String getNombreProducto() {
        return nombreProducto;
    }
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }
    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }
    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }
    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }
    public BigDecimal getSubTotal() {
        return subTotal;
    }
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    // Getters y Setters (mantén los existentes y añade el nuevo)
  

}