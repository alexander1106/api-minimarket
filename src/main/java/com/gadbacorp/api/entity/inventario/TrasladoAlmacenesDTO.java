package com.gadbacorp.api.entity.inventario;

import java.time.LocalDateTime;

public class TrasladoAlmacenesDTO {
     private Integer idtraslado;
    private Integer idproducto;
    private Integer origenId;
    private Integer destinoId;
    private Integer cantidad;
    private LocalDateTime fechaTraslado;

    public TrasladoAlmacenesDTO() { }

    public Integer getIdtraslado() {
        return idtraslado;
    }

    public void setIdtraslado(Integer idtraslado) {
        this.idtraslado = idtraslado;
    }

    public Integer getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
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
        return "TrasladoAlmacenDTO [idtraslado=" + idtraslado + ", idproducto=" + idproducto + ", origenId=" + origenId
                + ", destinoId=" + destinoId + ", cantidad=" + cantidad + ", fechaTraslado=" + fechaTraslado + "]";
    }
}
