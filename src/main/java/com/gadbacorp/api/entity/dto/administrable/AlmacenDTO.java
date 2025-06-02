package com.gadbacorp.api.entity.dto.administrable;

public class AlmacenDTO {
private Integer idAlmacen;
private String nombre;
// Constructor
public AlmacenDTO(Integer idAlmacen, String nombre) {
    this.idAlmacen = idAlmacen;
    this.nombre = nombre;
}

// Getters y Setters
public Integer getIdAlmacen() {
    return idAlmacen;
}

public void setIdAlmacen(Integer idAlmacen) {
    this.idAlmacen = idAlmacen;
}

public String getNombre() {
    return nombre;
}

public void setNombre(String nombre) {
    this.nombre = nombre;
}
}