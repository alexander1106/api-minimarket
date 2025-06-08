package com.gadbacorp.api.entity.inventario;

import java.time.LocalDateTime;

public class TrasladoInventarioProductoDTO {
    private Integer idtraslado;
    private Integer origenId;    // corresponde a idinventarioproducto_origen
    private Integer destinoId;   // corresponde a idinventarioproducto_destino
    private Integer cantidad;
    private LocalDateTime fechaTraslado;

    public TrasladoInventarioProductoDTO() { }

    public TrasladoInventarioProductoDTO(
            Integer idtraslado,
            Integer origenId,
            Integer destinoId,
            Integer cantidad,
            LocalDateTime fechaTraslado
    ) {
        this.idtraslado = idtraslado;
        this.origenId = origenId;
        this.destinoId = destinoId;
        this.cantidad = cantidad;
        this.fechaTraslado = fechaTraslado;
    }

    public Integer getIdtraslado() {
        return idtraslado;
    }
    public void setIdtraslado(Integer idtraslado) {
        this.idtraslado = idtraslado;
    }

    public Integer getOrigenId() {
        return origenId;
    }
    public void setOrigenId(Integer origenId) {
        this.origenId = origenId;
    }

    public Integer getDestinoId() {
        return destinoId;
    }
    public void setDestinoId(Integer destinoId) {
        this.destinoId = destinoId;
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

    @Override
    public String toString() {
        return "TrasladoInventarioProductoDTO ["
            + "idtraslado=" + idtraslado
            + ", origenId=" + origenId
            + ", destinoId=" + destinoId
            + ", cantidad=" + cantidad
            + ", fechaTraslado=" + fechaTraslado
            + "]";
    }
}
