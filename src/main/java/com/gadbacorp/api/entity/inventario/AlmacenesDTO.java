package com.gadbacorp.api.entity.inventario;

public class AlmacenesDTO {
private Integer idalmacen;
    private String nombre;
    private String descripcion;
    private Integer estado = 1;
    private Integer idSucursal;

    public Integer getIdalmacen() {
        return idalmacen;
    }
    public void setIdalmacen(Integer idalmacen) {
        this.idalmacen = idalmacen;
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
    public Integer getIdSucursal() {
        return idSucursal;
    }
    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }
    @Override
    public String toString() {
        return "AlmacenesDTO [idalmacen=" + idalmacen + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", estado=" + estado + ", idSucursal=" + idSucursal + "]";
    }
    
}