package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "productos")
@SQLDelete(sql="UPDATE productos SET estado = 0 WHERE idproducto = ?")
@Where(clause = "estado = 1")
public class Productos {
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
    private Integer estado = 1;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Categorias id_categoria;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "id_unidad_medida")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UnidadDeMedida id_unidad_medida;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "id_tipo_producto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TipoProducto id_tipo_producto;
    
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"producto", "almacen"})
    private List<AlmacenProducto> almacenProductos;

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

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Categorias getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(Categorias id_categoria) {
        this.id_categoria = id_categoria;
    }

    public UnidadDeMedida getId_unidad_medida() {
        return id_unidad_medida;
    }

    public void setId_unidad_medida(UnidadDeMedida id_unidad_medida) {
        this.id_unidad_medida = id_unidad_medida;
    }

    public TipoProducto getId_tipo_producto() {
        return id_tipo_producto;
    }

    public void setId_tipo_producto(TipoProducto id_tipo_producto) {
        this.id_tipo_producto = id_tipo_producto;
    }

    public List<AlmacenProducto> getAlmacenProducto() {
        return almacenProductos;
    }

    public void setAlmacenProducto(List<AlmacenProducto> almacenProducto) {
        this.almacenProductos = almacenProducto;
    }

    @Override
    public String toString() {
        return "Productos [idproducto=" + idproducto + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", fechaVencimiento=" + fechaVencimiento + ", tipoImpuesto=" + tipoImpuesto + ", costoCompra="
                + costoCompra + ", costoVenta=" + costoVenta + ", costoMayor=" + costoMayor + ", estado=" + estado
                + ", id_categoria=" + id_categoria + ", id_unidad_medida=" + id_unidad_medida + ", id_tipo_producto="
                + id_tipo_producto + ", almacenProducto=" + almacenProductos + "]";
    }

    
    
}
