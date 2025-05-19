package com.gadbacorp.api.entity.inventario;

import java.util.List;

public class InventarioDTO {
    private Integer idinventario;
    private Integer idproducto;
    private Integer idalmacen;
    private Integer stock;
    private List<AjusteInventarioDTO> ajustes;

    public InventarioDTO() { }

    public Integer getIdinventario() {
        return idinventario;
    }
    public void setIdinventario(Integer idinventario) {
        this.idinventario = idinventario;
    }
    public Integer getIdproducto() {
        return idproducto;
    }
    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
    }
    public Integer getIdalmacen() {
        return idalmacen;
    }
    public void setIdalmacen(Integer idalmacen) {
        this.idalmacen = idalmacen;
    }
    public Integer getStock() {
        return stock;
    }
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    public List<AjusteInventarioDTO> getAjustes() {
        return ajustes;
    }
    public void setAjustes(List<AjusteInventarioDTO> ajustes) {
        this.ajustes = ajustes;
    }

    @Override
    public String toString() {
        return "InventarioDTO [idinventario=" + idinventario +
               ", idproducto=" + idproducto +
               ", idalmacen=" + idalmacen +
               ", stock=" + stock +
               ", ajustes=" + ajustes + "]";
    }
}