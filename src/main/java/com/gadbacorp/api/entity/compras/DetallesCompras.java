package com.gadbacorp.api.entity.compras;

import jakarta.persistence.*;
import java.math.BigDecimal;

import com.gadbacorp.api.entity.inventario.Productos;

@Entity
@Table(name = "detalle_compras")
public class DetallesCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_detalle_compra")
    private Integer idDetalleCompra;

    @ManyToOne
    @JoinColumn(name = "Id_compra")
    private Compras compra;

    @ManyToOne
    @JoinColumn(name = "Id_Producto")
    private Productos producto;

    private Integer cantidad;

    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;

    private BigDecimal subtotal;

    public Integer getIdDetalleCompra() {
        return idDetalleCompra;
    }

    public void setIdDetalleCompra(Integer idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
    }

    public Compras getCompra() {
        return compra;
    }

    public void setCompra(Compras compra) {
        this.compra = compra;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
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

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "DetallesCompras [idDetalleCompra=" + idDetalleCompra + ", compra=" + compra + ", producto=" + producto
                + ", cantidad=" + cantidad + ", precioUnitario=" + precioUnitario + ", subtotal=" + subtotal + "]";
    }
    
}