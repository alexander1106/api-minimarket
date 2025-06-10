package com.gadbacorp.api.entity.inventario;

public class UnidadDeMedidaDTO {
private Integer idunidadmedida;
    private String nombre;

    public UnidadDeMedidaDTO() {}

    public UnidadDeMedidaDTO(Integer idunidadmedida, String nombre) {
        this.idunidadmedida = idunidadmedida;
        this.nombre = nombre;
    }

    public Integer getIdunidadmedida() {
        return idunidadmedida;
    }

    public void setIdunidadmedida(Integer idunidadmedida) {
        this.idunidadmedida = idunidadmedida;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "UnidadDeMedidaDTO [idunidadmedida=" + idunidadmedida + ", nombre=" + nombre + "]";
    }
}