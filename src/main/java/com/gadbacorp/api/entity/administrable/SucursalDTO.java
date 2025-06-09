package com.gadbacorp.api.entity.administrable;

import java.util.List;

import com.gadbacorp.api.entity.inventario.AlmacenesDTO;

public class SucursalDTO {
    private Integer idSucursal;
    private String nombreSucursal;
    private String contacto;
    private String direccion;
    private EmpresaDTO empresa;
    private Integer estado=1;
    private List<AlmacenesDTO> almacenes;

    // Constructores
    public SucursalDTO() {}

    public SucursalDTO(Integer idSucursal, String nombreSucursal, String contacto, String direccion, EmpresaDTO empresa,
            Integer estado, List<AlmacenesDTO> almacenes) {
        this.idSucursal = idSucursal;
        this.nombreSucursal = nombreSucursal;
        this.contacto = contacto;
        this.direccion = direccion;
        this.empresa = empresa;
        this.estado = estado;
        this.almacenes = almacenes;
    }



    // Getters y Setters
    public Integer getIdSucursal() { return idSucursal; }
    public void setIdSucursal(Integer idSucursal) { this.idSucursal = idSucursal; }
    public String getNombreSucursal() { return nombreSucursal; }
    public void setNombreSucursal(String nombreSucursal) { this.nombreSucursal = nombreSucursal; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public EmpresaDTO getEmpresa() { return empresa; }
    public void setEmpresa(EmpresaDTO empresa) { this.empresa = empresa; }
    public List<AlmacenesDTO> getAlmacenes() { return almacenes; }
    public void setAlmacenes(List<AlmacenesDTO> almacenes) { this.almacenes = almacenes; }

    @Override
    public String toString() {
        return "SucursalDTO{" +
                "idSucursal=" + idSucursal +
                ", nombreSucursal='" + nombreSucursal + '\'' +
                ", empresa=" + (empresa != null ? empresa.getRazonSocial() : "null") +
                ", almacenes=" + (almacenes != null ? almacenes.size() : 0) +
                '}';
    }



    public Integer getEstado() {
        return estado;
    }



    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}