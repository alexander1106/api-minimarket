package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Forma en que se compra o se vende un producto, expresada en su unidad base.
 * Ej.: producto Arroz (base KG) -> "Saco 50 kg", factor 50; producto Galletas (base UND) -> "Paquete x12", factor 12.
 * El stock siempre se guarda en la unidad base; el factor convierte cada movimiento.
 */
@Entity
@Table(name = "producto_presentacion")
@SQLDelete(sql = "UPDATE producto_presentacion SET estado = 0 WHERE id_presentacion = ?")
@Where(clause = "estado = 1")
public class ProductoPresentacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_presentacion")
    private Integer idPresentacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproducto", nullable = false)
    @JsonIgnore
    private Productos producto;

    @Column(length = 80, nullable = false)
    private String nombre;

    /** Cuantas unidades base contiene (50 para un saco de 50 kg). Siempre mayor a 0. */
    @Column(precision = 14, scale = 3, nullable = false)
    private BigDecimal factor;

    @Column(name = "precio_venta", precision = 14, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "precio_compra", precision = 14, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "codigo_barras", length = 40)
    private String codigoBarras;

    private Integer estado = 1;

    public ProductoPresentacion() {
    }

    public Integer getIdPresentacion() { return idPresentacion; }
    public void setIdPresentacion(Integer idPresentacion) { this.idPresentacion = idPresentacion; }
    public Productos getProducto() { return producto; }
    public void setProducto(Productos producto) { this.producto = producto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getFactor() { return factor; }
    public void setFactor(BigDecimal factor) { this.factor = factor; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }
}
