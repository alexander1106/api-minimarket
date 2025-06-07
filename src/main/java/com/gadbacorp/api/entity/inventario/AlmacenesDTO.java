package com.gadbacorp.api.entity.inventario;

public class AlmacenesDTO {
    private Integer idalmacen;
    private String nombre;
    private String descripcion;
    private String direccion;
    private Integer idEmpleado;
    private Integer estado = 1;
    private Integer idSucursal;
    
    public AlmacenesDTO() { }

    // constructor incluyendo idEmpleado
    public AlmacenesDTO(Integer idalmacen,
                        String nombre,
                        String descripcion,
                        String direccion,
                        Integer idEmpleado,
                        Integer idSucursal) {
        this.idalmacen   = idalmacen;
        this.nombre      = nombre;
        this.descripcion = descripcion;
        this.direccion   = direccion;
        this.idEmpleado  = idEmpleado;
        this.idSucursal  = idSucursal;
    }

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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(Integer idEmpleado) {
        this.idEmpleado = idEmpleado;
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
                + ", direccion=" + direccion + ", idEmpleado=" + idEmpleado + ", estado=" + estado + ", idSucursal="
                + idSucursal + "]";
    }
    
    
}