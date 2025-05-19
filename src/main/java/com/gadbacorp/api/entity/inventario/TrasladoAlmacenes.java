package com.gadbacorp.api.entity.inventario;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "traslado_almacen")
@SQLDelete(sql = "UPDATE traslado_almacen SET estado = 0 WHERE idtraslado = ?")
@Where(clause = "estado = 1")
public class TrasladoAlmacenes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idtraslado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproducto", nullable = false)
    private Productos producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_origen", nullable = false)
    private Almacenes origen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "almacen_destino", nullable = false)
    private Almacenes destino;

    private Integer cantidad;
    private LocalDateTime fechaTraslado;
    private Integer estado = 1;

    public TrasladoAlmacenes() { }

    public TrasladoAlmacenes(Productos producto, Almacenes origen, Almacenes destino, Integer cantidad, LocalDateTime fecha) {
        this.producto = producto;
        this.origen = origen;
        this.destino = destino;
        this.cantidad = cantidad;
        this.fechaTraslado = fecha;
    }

    // —— Getters y Setters ——

    public Integer getIdtraslado() {
        return idtraslado;
    }

    public void setIdtraslado(Integer idtraslado) {
        this.idtraslado = idtraslado;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }

    public Almacenes getOrigen() {
        return origen;
    }

    public void setOrigen(Almacenes origen) {
        this.origen = origen;
    }

    public Almacenes getDestino() {
        return destino;
    }

    public void setDestino(Almacenes destino) {
        this.destino = destino;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public LocalDateTime getFechaTraslado() {
        return fechaTraslado;
    }

    public void setFechaTraslado(LocalDateTime fechaTraslado) {
        this.fechaTraslado = fechaTraslado;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "TrasladoAlmacenes [idtraslado=" + idtraslado +
               ", producto=" + (producto != null ? producto.getIdproducto() : null) +
               ", origen=" + (origen != null ? origen.getIdalmacen() : null) +
               ", destino=" + (destino != null ? destino.getIdalmacen() : null) +
               ", cantidad=" + cantidad +
               ", fechaTraslado=" + fechaTraslado + "]";
    }
}