package com.gadbacorp.api.entity.compras;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gadbacorp.api.entity.inventario.Productos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "detalle_compras")
@SQLDelete(sql = "UPDATE detalle_compras SET estado=0 WHERE id_detalle_compra = ?")
@Where(clause = "estado=1")
public class DetallesCompras {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_compra")
    private Integer idDetalleCompra;

    @ManyToOne
    @JoinColumn(name = "id_compra")
    @JsonIgnoreProperties({"detalles"}) 
    private Compras compra;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Productos producto;

    @jakarta.persistence.Column(precision = 14, scale = 3)

    private BigDecimal cantidad;

    /** Presentacion en la que se expreso la cantidad (saco, paquete...); null = unidad base. */
    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "id_presentacion")
    private com.gadbacorp.api.entity.inventario.ProductoPresentacion presentacion;

    /** La cantidad convertida a la unidad base del producto (la que mueve el stock). */
    @jakarta.persistence.Column(name = "cantidad_base", precision = 14, scale = 3)
    private BigDecimal cantidadBase;

    public com.gadbacorp.api.entity.inventario.ProductoPresentacion getPresentacion() { return presentacion; }
    public void setPresentacion(com.gadbacorp.api.entity.inventario.ProductoPresentacion presentacion) { this.presentacion = presentacion; }
    public BigDecimal getCantidadBase() { return cantidadBase; }
    public void setCantidadBase(BigDecimal cantidadBase) { this.cantidadBase = cantidadBase; }

    /** Cantidad en unidad base; para lineas anteriores a las presentaciones es la propia cantidad. */

    /** Lote y vencimiento de lo comprado en esta linea (opcionales, para perecibles). */
    @jakarta.persistence.Column(name = "lote", length = 40)
    private String lote;
    @jakarta.persistence.Column(name = "fecha_vencimiento")
    private java.time.LocalDate fechaVencimiento;

    @com.fasterxml.jackson.annotation.JsonIgnore
    public BigDecimal cantidadEnBase() {
        return cantidadBase != null ? cantidadBase : cantidad;
    }
    
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;
    
    @Column(name = "precio_compra")
    private BigDecimal precioCompra;
    
    @Column(name = "precio_venta")
    private BigDecimal precioVenta;
    
    @Column(name = "subtotal")
    private BigDecimal subTotal;
    private Integer estado = 1;

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

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
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

    /** Presentacion en la que viene la cantidad al crear/editar la linea (no se guarda: se resuelve a presentacion + cantidadBase). */
    @jakarta.persistence.Transient
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Integer idPresentacionSolicitada;

    public Integer getIdPresentacionSolicitada() { return idPresentacionSolicitada; }
    public void setIdPresentacionSolicitada(Integer idPresentacionSolicitada) { this.idPresentacionSolicitada = idPresentacionSolicitada; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
    public java.time.LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(java.time.LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
