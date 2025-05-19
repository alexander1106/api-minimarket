package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "promociones")
@SQLDelete(sql = "UPDATE promociones SET estado = 0 WHERE idpromocion = ?")
@Where(clause = "estado = 1")
public class Promociones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idpromocion;
    private String nombre;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String tipoDescuento;
    private BigDecimal valor;
    private Integer estado = 1;

    /** Relación con Productos: se crea la tabla promocion_producto */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "promocion_producto",
        joinColumns = @JoinColumn(name = "idpromocion"),
        inverseJoinColumns = @JoinColumn(name = "idproducto")
    )
    private List<Productos> productos = new ArrayList<>();

    /** Relación con Categorías: se crea la tabla promocion_categoria */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "promocion_categoria",
        joinColumns = @JoinColumn(name = "idpromocion"),
        inverseJoinColumns = @JoinColumn(name = "idcategoria")
    )
    private List<Categorias> categorias = new ArrayList<>();

    public Promociones() {
    }

    public Integer getIdpromocion() {
        return idpromocion;
    }

    public void setIdpromocion(Integer idpromocion) {
        this.idpromocion = idpromocion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getTipoDescuento() {
        return tipoDescuento;
    }

    public void setTipoDescuento(String tipoDescuento) {
        this.tipoDescuento = tipoDescuento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public List<Productos> getProductos() {
        return productos;
    }

    public void setProductos(List<Productos> productos) {
        this.productos = productos;
    }

    public List<Categorias> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categorias> categorias) {
        this.categorias = categorias;
    }

    @Override
    public String toString() {
        return "Promocion [idpromocion=" + idpromocion + ", nombre=" + nombre + ", fechaInicio=" + fechaInicio +
               ", fechaFin=" + fechaFin + ", tipoDescuento=" + tipoDescuento + ", valor=" + valor + "]";
    }
}