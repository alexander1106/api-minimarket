package com.gadbacorp.api.entity.inventario;

public class AlmacenesDTO {
    private Integer idalmacen;
    private String nombre;
    private String descripcion;
    private String direccion;
    private Integer estado = 1;
    private Integer idSucursal;
    private String encargado;
    
    public AlmacenesDTO() { }

    // constructor incluyendo idEmpleado
 

    public Integer getIdalmacen() {
        return idalmacen;
    }

    public AlmacenesDTO(Integer idalmacen, String nombre, String descripcion, String direccion, Integer estado,
            Integer idSucursal, String encargado) {
        this.idalmacen = idalmacen;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.estado = estado;
        this.idSucursal = idSucursal;
        this.encargado = encargado;
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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


    public String getEncargado() {
        return encargado;
    }

    public void setEncargado(String encargado) {
        this.encargado = encargado;
    }
    
    
}