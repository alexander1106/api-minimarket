package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;
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
 * Una fila del kardex: cada vez que el stock de un producto sube o baja en una sucursal queda registrado
 * con su motivo, la cantidad (en unidad base, con signo) y el stock en que quedo la sucursal.
 * Es solo de lectura: nunca se edita ni se borra.
 */
@Entity
@Table(name = "movimiento_stock", indexes = {
        @Index(name = "idx_mov_producto_sucursal", columnList = "idproducto,id_sucursal,fecha")})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MovimientoStock {

    public static final String COMPRA = "COMPRA";
    public static final String ANULACION_COMPRA = "ANULACION_COMPRA";
    public static final String VENTA = "VENTA";
    public static final String ANULACION_VENTA = "ANULACION_VENTA";
    public static final String DEVOLUCION = "DEVOLUCION";
    public static final String AJUSTE = "AJUSTE";
    public static final String TRASLADO_SALIDA = "TRASLADO_SALIDA";
    public static final String TRASLADO_ENTRADA = "TRASLADO_ENTRADA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;

    private LocalDateTime fecha;

    @Column(length = 30)
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproducto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Productos producto;

    @Column(name = "id_sucursal")
    private Integer idSucursal;

    /** Positiva = entra stock, negativa = sale. Unidad base del producto. */
    @Column(name = "cantidad_base", precision = 14, scale = 3)
    private BigDecimal cantidadBase;

    /** Stock total del producto en la sucursal despues del movimiento. */
    @Column(name = "stock_resultante", precision = 14, scale = 3)
    private BigDecimal stockResultante;

    @Column(length = 120)
    private String referencia;

    @Column(length = 80)
    private String usuario;

    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public BigDecimal getCantidadBase() {
        return cantidadBase;
    }

    public void setCantidadBase(BigDecimal cantidadBase) {
        this.cantidadBase = cantidadBase;
    }

    public BigDecimal getStockResultante() {
        return stockResultante;
    }

    public void setStockResultante(BigDecimal stockResultante) {
        this.stockResultante = stockResultante;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
