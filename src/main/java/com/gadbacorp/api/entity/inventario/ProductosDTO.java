package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class ProductosDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idproducto;
    private String nombre;
    private String descripcion;
    private LocalDate fechaVencimiento;
    private String tipoImpuesto;
    private BigDecimal costoCompra;
    private BigDecimal costoVenta;
    private BigDecimal costoMayor;
    private String imagen;
    private Integer idcategoria;
    private Integer idunidadmedida;
    private Integer idtipoproducto;
    
    private List<InventarioProductosDTO> inventarioProducto;

    public ProductosDTO() { }

    public Integer getIdproducto() {
        return idproducto;
    }

    public void setIdproducto(Integer idproducto) {
        this.idproducto = idproducto;
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

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getTipoImpuesto() {
        return tipoImpuesto;
    }

    public void setTipoImpuesto(String tipoImpuesto) {
        this.tipoImpuesto = tipoImpuesto;
    }

    public BigDecimal getCostoCompra() {
        return costoCompra;
    }

    public void setCostoCompra(BigDecimal costoCompra) {
        this.costoCompra = costoCompra;
    }

    public BigDecimal getCostoVenta() {
        return costoVenta;
    }

    public void setCostoVenta(BigDecimal costoVenta) {
        this.costoVenta = costoVenta;
    }

    public BigDecimal getCostoMayor() {
        return costoMayor;
    }

    public void setCostoMayor(BigDecimal costoMayor) {
        this.costoMayor = costoMayor;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(Integer idcategoria) {
        this.idcategoria = idcategoria;
    }

    public Integer getIdunidadmedida() {
        return idunidadmedida;
    }

    public void setIdunidadmedida(Integer idunidadmedida) {
        this.idunidadmedida = idunidadmedida;
    }

    public Integer getIdtipoproducto() {
        return idtipoproducto;
    }

    public void setIdtipoproducto(Integer idtipoproducto) {
        this.idtipoproducto = idtipoproducto;
    }

    public List<InventarioProductosDTO> getInventarioProducto() {
        return inventarioProducto;
    }

    public void setInventarioProducto(List<InventarioProductosDTO> inventarioProducto) {
        this.inventarioProducto = inventarioProducto;
    }

    @Override
    public String toString() {
        return "ProductosDTO [idproducto=" + idproducto + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", fechaVencimiento=" + fechaVencimiento + ", tipoImpuesto=" + tipoImpuesto + ", costoCompra="
                + costoCompra + ", costoVenta=" + costoVenta + ", costoMayor=" + costoMayor + ", imagen=" + imagen
                + ", idcategoria=" + idcategoria + ", idunidadmedida=" + idunidadmedida + ", idtipoproducto="
                + idtipoproducto + ", inventarioProducto=" + inventarioProducto + "]";
    }

    

}
