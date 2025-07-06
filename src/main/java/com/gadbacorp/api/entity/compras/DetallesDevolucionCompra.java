package com.gadbacorp.api.entity.compras;

import jakarta.persistence.*;
import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.gadbacorp.api.entity.inventario.Productos;

@Entity
@Table(name = "detalles_devoluciones_compras")
@SQLDelete(sql = "UPDATE detalles_devoluciones_compras SET estado=0 WHERE id_detalle_devoluciones = ?")
@Where(clause = "estado=1")
public class DetallesDevolucionCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_devoluciones")
    private Integer idDetalleDevolucion;

    private Integer cantidad;
    private Integer estado = 1;
    private BigDecimal precioUnitario;
    private BigDecimal subTotal;

    @ManyToOne
    @JoinColumn(name = "id_devolucionCompras")
    private DevolucionesCompra devolucionCompra;

    @ManyToOne
    @JoinColumn(name = "idproducto")
    private Productos producto;

    // Getters y Setters
    public Integer getIdDetalleDevolucion() {
        return idDetalleDevolucion;
    }

    public void setIdDetalleDevolucion(Integer idDetalleDevolucion) {
        this.idDetalleDevolucion = idDetalleDevolucion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public DevolucionesCompra getDevolucionCompra() {
        return devolucionCompra;
    }

    public void setDevolucionCompra(DevolucionesCompra devolucionCompra) {
        this.devolucionCompra = devolucionCompra;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }
}