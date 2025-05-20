package com.gadbacorp.api.entity.inventario;

public class CategoriasDTO {
    private Integer idcategoria;
    private String nombre;

    public CategoriasDTO() {}

    public CategoriasDTO(Integer idcategoria, String nombre) {
        this.idcategoria = idcategoria;
        this.nombre = nombre;
    }

    public Integer getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(Integer idcategoria) {
        this.idcategoria = idcategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "CategoriasDTO [idcategoria=" + idcategoria + ", nombre=" + nombre + "]";
    }
}

