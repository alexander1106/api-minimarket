package com.gadbacorp.api.entity.inventario;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "idcategoria", "nombre", "imagen", "productos" })
public class CategoriasDTO {
    private Integer idcategoria;
    private String nombre;
    private String imagen;

    /** Solo los IDs de producto */
    private List<Integer> productos = new ArrayList<>();

    public CategoriasDTO() {}

    public CategoriasDTO(Integer idcategoria, String nombre, String imagen) {
        this.idcategoria = idcategoria;
        this.nombre      = nombre;
        this.imagen      = imagen;
    }

    // Si quieres también inicializar con lista de IDs:
    public CategoriasDTO(Integer idcategoria, String nombre, String imagen, List<Integer> productos) {
        this.idcategoria = idcategoria;
        this.nombre      = nombre;
        this.imagen      = imagen;
        this.productos   = productos;
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

    public List<Integer> getProductos() {
        return productos;
    }

    public void setProductos(List<Integer> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "CategoriasDTO [idcategoria=" + idcategoria +
               ", nombre=" + nombre +
               ", imagen=" + imagen +
               ", productos=" + productos + "]";
    }
}
