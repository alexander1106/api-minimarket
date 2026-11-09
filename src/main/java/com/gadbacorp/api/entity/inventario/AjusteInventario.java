package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ajuste_inventario")
@SQLDelete(sql = "UPDATE ajuste_inventario SET estado = 0 WHERE idajusteinventario = ?")
@Where(clause = "estado = 1")
public class AjusteInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idajusteinventario;

    @jakarta.persistence.Column(precision = 14, scale = 3)

    private BigDecimal cantidad;
    private LocalDateTime fechaAjuste;
    private String descripcion;
    private Integer estado = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idinventarioproducto", nullable = false)
    @JsonIgnore
    private InventarioProducto inventarioProducto;

    public AjusteInventario() { }

    public AjusteInventario(BigDecimal cantidad, LocalDateTime fechaAjuste, String descripcion) {
        this.cantidad = cantidad;
        this.fechaAjuste = fechaAjuste;
        this.descripcion = descripcion;
    }

    public Integer getIdajusteinventario() {
        return idajusteinventario;
    }

    public void setIdajusteinventario(Integer idajusteinventario) {
        this.idajusteinventario = idajusteinventario;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaAjuste() {
        return fechaAjuste;
    }

    public void setFechaAjuste(LocalDateTime fechaAjuste) {
        this.fechaAjuste = fechaAjuste;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public InventarioProducto getInventarioProducto() {
        return inventarioProducto;
    }

    public void setInventarioProducto(InventarioProducto inventarioProducto) {
        this.inventarioProducto = inventarioProducto;
    }

    @Override
    public String toString() {
        return "AjusteInventario [id=" + idajusteinventario +
               ", cantidad=" + cantidad +
               ", fechaAjuste=" + fechaAjuste +
               ", descripcion=" + descripcion + "]";
    }
}
