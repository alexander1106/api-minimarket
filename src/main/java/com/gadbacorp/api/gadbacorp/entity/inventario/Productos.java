package com.gadbacorp.api.gadbacorp.entity.inventario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gadbacorp.api.gadbacorp.entity.ventas.DetallesVentas;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
    @Column(name = "id_producto")
    private Integer idproducto;

    private String nombre;
    private String descripcion;
    private LocalDate fechaVencimiento;
    private String tipoImpuesto;
    private BigDecimal costoCompra;
    private BigDecimal costoVenta;
    private BigDecimal costoMayor;
    private String imagen;
    private Integer estado = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategoria")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Categorias categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idunidadmedida")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private UnidadDeMedida unidadMedida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtipoproducto")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private TipoProducto tipoProducto;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<AlmacenProducto> almacenProductos = new ArrayList<>();


    @OneToMany(mappedBy = "productos", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<DetallesVentas> detallesVentas;
    


    public Productos() { }

    public Productos(Integer id) {
        this.idproducto = id;
    }

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

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Categorias getCategoria() {
        return categoria;
    }

    public void setCategoria(Categorias categoria) {
        this.categoria = categoria;
    }

    public UnidadDeMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadDeMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(TipoProducto tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public List<AlmacenProducto> getAlmacenProductos() {
        return almacenProductos;
    }

    public void setAlmacenProductos(List<AlmacenProducto> almacenProductos) {
        this.almacenProductos = almacenProductos;
    }

    public List<DetallesVentas> getDetallesVentas() {
        return detallesVentas;
    }

    public void setDetallesVentas(List<DetallesVentas> detallesVentas) {
        this.detallesVentas = detallesVentas;
    }

    
}