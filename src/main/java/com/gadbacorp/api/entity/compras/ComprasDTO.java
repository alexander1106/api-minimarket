package com.gadbacorp.api.entity.compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ComprasDTO {
    private Integer idCompra;
    private Integer idProveedor;
    private BigDecimal total;
    private String descripcion;
    private LocalDateTime fechaCompra;
     private Integer estado = 1;
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
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }
    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public List<DetallesComprasDTO> getDetalles() {
        return detalles;
    }
    public void setDetalles(List<DetallesComprasDTO> detalles) {
        this.detalles = detalles;
    }
    
    // Getters y Setters
    
    
}