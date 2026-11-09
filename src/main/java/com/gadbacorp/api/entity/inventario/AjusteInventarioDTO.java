package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AjusteInventarioDTO {
    private Integer idajusteinventario;
    private Integer idinventarioproducto;  // Cambiado para referenciar InventarioProducto
    private BigDecimal cantidad;
    private String descripcion;
    private LocalDateTime fechaAjuste;

    public AjusteInventarioDTO() { }

    public AjusteInventarioDTO(
        Integer idajusteinventario,
        Integer idinventarioproducto,
        BigDecimal cantidad,
        String descripcion,
        LocalDateTime fechaAjuste
    ) {
        this.idajusteinventario = idajusteinventario;
        this.idinventarioproducto = idinventarioproducto;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.fechaAjuste = fechaAjuste;
    }

    public Integer getIdajusteinventario() {
        return idajusteinventario;
    }

    public void setIdajusteinventario(Integer idajusteinventario) {
        this.idajusteinventario = idajusteinventario;
    }

    public Integer getIdinventarioproducto() {
        return idinventarioproducto;
    }

    public void setIdinventarioproducto(Integer idinventarioproducto) {
        this.idinventarioproducto = idinventarioproducto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaAjuste() {
        return fechaAjuste;
    }

    public void setFechaAjuste(LocalDateTime fechaAjuste) {
        this.fechaAjuste = fechaAjuste;
    }

    @Override
    public String toString() {
        return "AjusteInventarioDTO ["
            + "idajusteinventario=" + idajusteinventario
            + ", idinventarioproducto=" + idinventarioproducto
            + ", cantidad=" + cantidad
            + ", descripcion=" + descripcion
            + ", fechaAjuste=" + fechaAjuste
            + "]";
    }
}
