package com.gadbacorp.api.entity.inventario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventarioProductosDTO {
    private Integer idinventarioproducto;
    private Integer stockactual;
    private LocalDateTime fechaingreso;
    private Integer idproducto;
    private Integer idinventario;
    private List<AjusteInventarioDTO> ajustes = new ArrayList<>(); // Lista de ajustes

    public InventarioProductosDTO() { }

    /**
     * Constructor para crear o actualizar (sin incluir ajustes).
     */
    public InventarioProductosDTO(
            Integer idinventarioproducto,
            Integer stockactual,
            LocalDateTime fechaingreso,
            Integer idproducto,
            Integer idinventario
    ) {
        this.idinventarioproducto = idinventarioproducto;
        this.stockactual = stockactual;
        this.fechaingreso = fechaingreso;
        this.idproducto = idproducto;
        this.idinventario = idinventario;
    }

    public Integer getIdinventarioproducto() {
        return idinventarioproducto;
    }

    public void setIdinventarioproducto(Integer idinventarioproducto) {
        this.idinventarioproducto = idinventarioproducto;
    }

    public Integer getStockactual() {
        return stockactual;
    }

    public void setStockactual(Integer stockactual) {
        this.stockactual = stockactual;
    }

    public LocalDateTime getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(LocalDateTime fechaingreso) {
        this.fechaingreso = fechaingreso;
    }

    public Integer getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }

    public Integer getIdinventario() {
        return idinventario;
    }

    public void setIdinventario(Integer idinventario) {
        this.idinventario = idinventario;
    }

    public List<AjusteInventarioDTO> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjusteInventarioDTO> ajustes) {
        this.ajustes = ajustes;
    }

    @Override
    public String toString() {
        return "InventarioProductosDTO ["
            + "idinventarioproducto=" + idinventarioproducto
            + ", stockactual=" + stockactual
            + ", fechaingreso=" + fechaingreso
            + ", idproducto=" + idproducto
            + ", idinventario=" + idinventario
            + ", ajustes=" + ajustes
            + "]";
    }
}
