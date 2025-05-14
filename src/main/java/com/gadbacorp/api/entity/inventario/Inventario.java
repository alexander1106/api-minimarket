package com.gadbacorp.api.entity.inventario;

import java.math.BigDecimal;

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
import jakarta.persistence.Table;

@Entity
@Table(name = "inventario")
@SQLDelete(sql="UPDATE productos SET estado = 0 WHERE idinventario = ?")
@Where(clause = "estado = 1")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idinventario;
    private String nombre;
    private Integer stock;
    private BigDecimal valorcompra;
    private BigDecimal costototal;
    private Integer estado = 1;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "idalmacen")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "almacenProducto"})
    private Almacenes id_almacen;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    @JsonIgnoreProperties({
    "hibernateLazyInitializer",
    "handler",
    "almacenProductos",
    "id_categoria",
    "id_unidad_medida",
    "id_tipo_producto"
    })
    private Productos id_producto;

    public Integer getIdinventario() {
        return idinventario;
    }

    public void setIdinventario(Integer idinventario) {
        this.idinventario = idinventario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getValorcompra() {
        return valorcompra;
    }

    public void setValorcompra(BigDecimal valorcompra) {
        this.valorcompra = valorcompra;
    }

    public BigDecimal getCostototal() {
        return costototal;
    }

    public void setCostototal(BigDecimal costototal) {
        this.costototal = costototal;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Almacenes getId_almacen() {
        return id_almacen;
    }

    public void setId_almacen(Almacenes id_almacen) {
        this.id_almacen = id_almacen;
    }
 
    public Productos getId_producto() {
        return id_producto;
    }

    public void setId_producto(Productos id_producto) {
        this.id_producto = id_producto;
    }

    @Override
    public String toString() {
        return "Inventario [idinventario=" + idinventario + ", nombre=" + nombre + ", stock=" + stock + ", valorcompra="
                + valorcompra + ", costototal=" + costototal + ", estado=" + estado + ", id_almacen=" + id_almacen
                + ", id_producto=" + id_producto + "]";
    }

    

}
