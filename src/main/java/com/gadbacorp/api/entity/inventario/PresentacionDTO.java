package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;

/** Presentacion de un producto (POST/PUT en la entrada; GET en la salida con los campos calculados). */
public class PresentacionDTO {

    private Integer idPresentacion;
    private Integer idProducto;
    private String nombre;
    private BigDecimal factor;
    private BigDecimal precioVenta;
    private BigDecimal precioCompra;
    private String codigoBarras;

    /** Codigo de la unidad base del producto (KG, UND...). */
    private String unidadBase;
    /** Texto listo para mostrar: "1 Saco 50 kg = 50 KG". */
    private String equivalencia;

    public Integer getIdPresentacion() { return idPresentacion; }
    public void setIdPresentacion(Integer idPresentacion) { this.idPresentacion = idPresentacion; }
    public Integer getIdProducto() { return idProducto; }
    public void setIdProducto(Integer idProducto) { this.idProducto = idProducto; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getFactor() { return factor; }
    public void setFactor(BigDecimal factor) { this.factor = factor; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }
    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public String getUnidadBase() { return unidadBase; }
    public void setUnidadBase(String unidadBase) { this.unidadBase = unidadBase; }
    public String getEquivalencia() { return equivalencia; }
    public void setEquivalencia(String equivalencia) { this.equivalencia = equivalencia; }
}
