package com.gadbacorp.api.entity.inventario;

import java.time.LocalDateTime;

public class AjusteInventarioDTO {
    private Integer idajusteinventario;
    private Integer idinventario;
    private Integer cantidad;
    private String descripcion;
    private LocalDateTime fechaAjuste;

    public AjusteInventarioDTO() { }

    public Integer getIdajusteinventario() {
        return idajusteinventario;
    }
    public void setIdajusteinventario(Integer idajusteinventario) {
        this.idajusteinventario = idajusteinventario;
    }
    public Integer getIdinventario() {
        return idinventario;
    }
    public void setIdinventario(Integer idinventario) {
        this.idinventario = idinventario;
    }
    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
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
        return "AjusteInventarioDTO [idajusteinventario=" + idajusteinventario +
               ", idinventario=" + idinventario +
               ", cantidad=" + cantidad +
               ", descripcion=" + descripcion +
               ", fechaAjuste=" + fechaAjuste + "]";
    }
}