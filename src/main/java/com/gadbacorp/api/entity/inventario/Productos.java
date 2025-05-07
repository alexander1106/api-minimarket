package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
public class Productos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer id;
    private String nombre;
    private String descripcion;
    private String imagen ;
    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;
    @Column(name = "tipo_impuesto")
    private String tipoImpuesto;
    @Column(name = "costo_compra")
    private BigDecimal costoCompra;
    @Column(name = "costo_venta")
    private BigDecimal costoVenta;
    @Column(name = "costo_mayor")
    private BigDecimal costoMayor;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categorias categoria;

    @ManyToOne
    @JoinColumn(name = "id_unidad_medida")
    private UnidadDeMedida unidaddemedida;

    @ManyToOne
    @JoinColumn(name = "id_tipo_producto")
    private TipoProducto tipoproducto;

    @ManyToOne
    @JoinColumn(name = "id_almacen")
    private Almacenes almacen;

    private Integer estado = 1;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
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
    public Categorias getCategoria() {
        return categoria;
    }
    public void setCategoria(Categorias categoria) {
        this.categoria = categoria;
    }
    public UnidadDeMedida getUnidaddemedida() {
        return unidaddemedida;
    }
    public void setUnidaddemedida(UnidadDeMedida unidaddemedida) {
        this.unidaddemedida = unidaddemedida;
    }
    public TipoProducto getTipoproducto() {
        return tipoproducto;
    }
    public void setTipoproducto(TipoProducto tipoproducto) {
        this.tipoproducto = tipoproducto;
    }
    public Almacenes getAlmacen() {
        return almacen;
    }
    public void setAlmacen(Almacenes almacen) {
        this.almacen = almacen;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    @Override
    public String toString() {
        return "Productos [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", imagen=" + imagen
                + ", fechaVencimiento=" + fechaVencimiento + ", tipoImpuesto=" + tipoImpuesto + ", costoCompra="
                + costoCompra + ", costoVenta=" + costoVenta + ", costoMayor=" + costoMayor + ", categoria=" + categoria
                + ", unidaddemedida=" + unidaddemedida + ", tipoproducto=" + tipoproducto + ", almacen=" + almacen
                + ", estado=" + estado + "]";
    }
    
}
