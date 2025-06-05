package com.gadbacorp.api.entity.dto.administrable;

import java.util.List;

public class EmpresaDTO {
    private Integer idEmpresa;
    private String razonSocial;
    private String ciudad;
    private String direccion;
    private Integer cantidadSucursales;
    private Integer cantidadTrabajadores;
    private Integer limiteInventario;
    private String correo;
    private byte[] logo; // Cambiado de Blob a byte[] para mejor serialización
    private List<SucursalDTO> sucursales;

    // Constructores
    public EmpresaDTO() {
    }

    public EmpresaDTO(Integer idEmpresa, String razonSocial, String ciudad, String direccion, 
                     Integer cantidadSucursales, Integer cantidadTrabajadores, 
                     Integer limiteInventario, String correo, byte[] logo) {
        this.idEmpresa = idEmpresa;
        this.razonSocial = razonSocial;
        this.ciudad = ciudad;
        this.direccion = direccion;
        this.cantidadSucursales = cantidadSucursales;
        this.cantidadTrabajadores = cantidadTrabajadores;
        this.limiteInventario = limiteInventario;
        this.correo = correo;
        this.logo = logo;
    }

    // Getters y Setters
    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getCantidadSucursales() {
        return cantidadSucursales;
    }

    public void setCantidadSucursales(Integer cantidadSucursales) {
        this.cantidadSucursales = cantidadSucursales;
    }

    public Integer getCantidadTrabajadores() {
        return cantidadTrabajadores;
    }

    public void setCantidadTrabajadores(Integer cantidadTrabajadores) {
        this.cantidadTrabajadores = cantidadTrabajadores;
    }

    public Integer getLimiteInventario() {
        return limiteInventario;
    }

    public void setLimiteInventario(Integer limiteInventario) {
        this.limiteInventario = limiteInventario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public List<SucursalDTO> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<SucursalDTO> sucursales) {
        this.sucursales = sucursales;
    }

    @Override
    public String toString() {
        return "EmpresaDTO{" +
                "idEmpresa=" + idEmpresa +
                ", razonSocial='" + razonSocial + '\'' +
                ", ciudad='" + ciudad + '\'' +
                ", direccion='" + direccion + '\'' +
                ", cantidadSucursales=" + cantidadSucursales +
                ", cantidadTrabajadores=" + cantidadTrabajadores +
                ", limiteInventario=" + limiteInventario +
                ", correo='" + correo + '\'' +
                '}'; // No incluimos el logo en el toString por ser un array de bytes
    }
}