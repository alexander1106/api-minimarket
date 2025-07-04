package com.gadbacorp.api.entity.compras;

import java.sql.Date;

public class ProveedorDTO {

    private Integer idProveedor;
    private String nombre;
    private String ruc;
    private String regimen;
    private String telefono;
    private String gmail;
    private String direccion;
    private Date fecha_registro;
    private Integer estado;
    private Integer idEmpresa;
    private String empresaNombre;  // ✅ Incluido

    public ProveedorDTO() {}

    // Getters y Setters
    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRegimen() { return regimen; }
    public void setRegimen(String regimen) { this.regimen = regimen; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getGmail() { return gmail; }
    public void setGmail(String gmail) { this.gmail = gmail; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Date getFecha_registro() { return fecha_registro; }
    public void setFecha_registro(Date fecha_registro) { this.fecha_registro = fecha_registro; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
}
