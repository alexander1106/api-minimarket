package com.gadbacorp.api.entity.inventario;

public class TipoProductoDTO {
    private Integer idtipoproducto;
    private String nombre;

    public TipoProductoDTO() {}

    public TipoProductoDTO(Integer idtipoproducto, String nombre) {
        this.idtipoproducto = idtipoproducto;
        this.nombre = nombre;
    }

    public Integer getIdtipoproducto() {
        return idtipoproducto;
    }

    public void setIdtipoproducto(Integer idtipoproducto) {
        this.idtipoproducto = idtipoproducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "TipoProductoDTO [idtipoproducto=" + idtipoproducto + ", nombre=" + nombre + "]";
    }
}

