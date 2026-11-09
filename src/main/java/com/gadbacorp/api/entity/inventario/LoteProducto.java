package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Un lote es lo que entro en una compra de un producto perecible: tiene su propia fecha de vencimiento y su propio saldo.
 * Las ventas consumen primero el lote que vence antes (FEFO). Cantidades siempre en unidad base.
 */
@Entity
@Table(name = "lote_producto", indexes = {
        @Index(name = "idx_lote_producto_sucursal", columnList = "idproducto,id_sucursal,fecha_vencimiento")})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LoteProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproducto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Productos producto;

    @Column(name = "id_sucursal")
    private Integer idSucursal;

    /** Codigo que trae el proveedor en el empaque; opcional. */
    @Column(length = 40)
    private String codigo;

    /** null = no vence. */
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "cantidad_inicial", precision = 14, scale = 3)
    private BigDecimal cantidadInicial;

    @Column(name = "cantidad_disponible", precision = 14, scale = 3)
    private BigDecimal cantidadDisponible;

    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;

    /** Linea de compra de la que salio; sirve para devolver el lote si se anula o corrige esa linea. */
    @Column(name = "id_detalle_compra")
    private Integer idDetalleCompra;

    @Column(length = 120)
    private String referencia;

    public Long getIdLote() {
        return idLote;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getCantidadInicial() {
        return cantidadInicial;
    }

    public void setCantidadInicial(BigDecimal cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }

    public BigDecimal getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(BigDecimal cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public LocalDateTime getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDateTime fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Integer getIdDetalleCompra() {
        return idDetalleCompra;
    }

    public void setIdDetalleCompra(Integer idDetalleCompra) {
        this.idDetalleCompra = idDetalleCompra;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
