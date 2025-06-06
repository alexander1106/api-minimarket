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
@Table(name = "traslado_inventario")
@SQLDelete(sql = "UPDATE traslado_inventario SET estado = 0 WHERE idtraslado = ?")
@Where(clause = "estado = 1")
public class TrasladoInventarioProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idtraslado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idinventarioproducto_origen", nullable = false)
    private InventarioProducto origen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idinventarioproducto_destino", nullable = false)
    private InventarioProducto destino;

    private Integer cantidad;
    private LocalDateTime fechaTraslado;
    private Integer estado = 1;

    public TrasladoInventarioProducto() { }

    public TrasladoInventarioProducto(
            InventarioProducto origen,
            InventarioProducto destino,
            Integer cantidad,
            LocalDateTime fechaTraslado
    ) {
        this.origen = origen;
        this.destino = destino;
        this.cantidad = cantidad;
        this.fechaTraslado = fechaTraslado;
    }

    public Integer getIdtraslado() {
        return idtraslado;
    }
    public void setIdtraslado(Integer idtraslado) {
        this.idtraslado = idtraslado;
    }

    public InventarioProducto getOrigen() {
        return origen;
    }
    public void setOrigen(InventarioProducto origen) {
        this.origen = origen;
    }

    public InventarioProducto getDestino() {
        return destino;
    }
    public void setDestino(InventarioProducto destino) {
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
        return "TrasladoInventarioProducto [idtraslado=" + idtraslado
                + ", origen=" + (origen != null ? origen.getIdinventarioproducto() : null)
                + ", destino=" + (destino != null ? destino.getIdinventarioproducto() : null)
                + ", cantidad=" + cantidad
                + ", fechaTraslado=" + fechaTraslado
                + ", estado=" + estado + "]";
    }
}
