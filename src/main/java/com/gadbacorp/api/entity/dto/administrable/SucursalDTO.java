package com.gadbacorp.api.entity.dto.administrable;

import java.util.List;

public class SucursalDTO {
private Integer idSucursal;
private String nombreSucursal;
private Integer contacto;
private String direccion;
// Empresa
private Integer idEmpresa;
private String razonSocialEmpresa;

// Lista de almacenes
private List<AlmacenDTO> almacenes;

// Getters y Setters
public Integer getIdSucursal() {
    return idSucursal;
}

public void setIdSucursal(Integer idSucursal) {
    this.idSucursal = idSucursal;
}

public String getNombreSucursal() {
    return nombreSucursal;
}

public void setNombreSucursal(String nombreSucursal) {
    this.nombreSucursal = nombreSucursal;
}

public Integer getContacto() {
    return contacto;
}

public void setContacto(Integer contacto) {
    this.contacto = contacto;
}

public String getDireccion() {
    return direccion;
}

public void setDireccion(String direccion) {
    this.direccion = direccion;
}

public Integer getIdEmpresa() {
    return idEmpresa;
}

public void setIdEmpresa(Integer idEmpresa) {
    this.idEmpresa = idEmpresa;
}

public String getRazonSocialEmpresa() {
    return razonSocialEmpresa;
}

public void setRazonSocialEmpresa(String razonSocialEmpresa) {
    this.razonSocialEmpresa = razonSocialEmpresa;
}

public List<AlmacenDTO> getAlmacenes() {
    return almacenes;
}

public void setAlmacenes(List<AlmacenDTO> almacenes) {
    this.almacenes = almacenes;
}
}