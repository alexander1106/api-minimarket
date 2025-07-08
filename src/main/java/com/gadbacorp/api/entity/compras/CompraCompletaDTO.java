package com.gadbacorp.api.entity.compras;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotNull;


public class CompraCompletaDTO {
    private Integer idCompra;
    
    @NotNull(message = "El proveedor es obligatorio")
    private Integer idProveedor;
    
    @NotNull(message = "El método de pago es obligatorio")
    private Integer idMetodoPago;
    
    private String descripcion;
    private BigDecimal total;
    
    @NotNull(message = "Debe incluir al menos un producto")
    private List<DetallesComprasDTO> detalles;

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdMetodoPago() {
        return idMetodoPago;
    }

    public void setIdMetodoPago(Integer idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetallesComprasDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallesComprasDTO> detalles) {
        this.detalles = detalles;
    }

    // Getters y Setters

    
}