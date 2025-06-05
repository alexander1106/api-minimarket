package com.gadbacorp.api.entity.dto.administrable;

import com.gadbacorp.api.entity.inventario.AlmacenesDTO;
import java.util.List;

public class SucursalDTO {
    private Integer idSucursal;
    private String nombreSucursal;
    private String contacto;
    private String direccion;
    private EmpresaDTO empresa;
    private List<AlmacenesDTO> almacenes;

    // Constructores
    public SucursalDTO() {}

    public SucursalDTO(Integer idSucursal, String nombreSucursal, String contacto, 
                      String direccion, EmpresaDTO empresa, List<AlmacenesDTO> almacenes) {
        this.idSucursal = idSucursal;
        this.nombreSucursal = nombreSucursal;
        this.contacto = contacto;
        this.direccion = direccion;
        this.empresa = empresa;
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
}