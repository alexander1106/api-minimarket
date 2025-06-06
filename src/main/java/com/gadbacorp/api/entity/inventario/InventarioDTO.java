package com.gadbacorp.api.entity.inventario;

public class InventarioDTO {
    private Integer idinventario;
    private String nombre;
    private String descripcion;
    private Integer estado = 1;
    private Integer idalmacen;

    public InventarioDTO() { }

    // Constructor para crear o actualizar
    public InventarioDTO(Integer idinventario, Integer idalmacen, String nombre, String descripcion, Integer estado) {
        this.idinventario = idinventario;
        this.idalmacen = idalmacen;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public Integer getIdinventario() {
        return idinventario;
    }

    public void setIdinventario(Integer idinventario) {
        this.idinventario = idinventario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getIdalmacen() {
        return idalmacen;
    }

    public void setIdalmacen(Integer idalmacen) {
        this.idalmacen = idalmacen;
    }


    @Override
    public String toString() {
        return "InventarioDTO [idinventario=" + idinventario + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", estado=" + estado + ", idalmacen=" + idalmacen + "]";
    }

    

}