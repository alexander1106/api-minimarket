package com.gadbacorp.api.entity.inventario;

public class CategoriasDTO {
    private Integer idcategoria;
    private String nombre;
    private String imagen;

    public CategoriasDTO() {}

    public CategoriasDTO(Integer idcategoria, String nombre, String imagen) {
        this.idcategoria = idcategoria;
        this.nombre = nombre;
        this.imagen = imagen;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    @Override
    public String toString() {
        return "CategoriasDTO [idcategoria=" + idcategoria + ", nombre=" + nombre + ", imagen=" + imagen + "]";
    }

    
}

