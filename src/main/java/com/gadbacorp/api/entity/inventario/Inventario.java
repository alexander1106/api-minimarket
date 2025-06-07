package com.gadbacorp.api.entity.inventario;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
@Table(name = "inventario")
@SQLDelete(sql = "UPDATE inventario SET estado = 0 WHERE idinventario = ?")
@Where(clause = "estado = 1")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idinventario;
    private String nombre;
    private String descripcion;
    private Integer stock;
    private Integer estado = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idalmacen", nullable = false)
    private Almacenes almacen;

    @OneToMany(mappedBy = "inventario",
        cascade = CascadeType.ALL,orphanRemoval = true,
        fetch = FetchType.LAZY)
    @JsonIgnore
    private List<InventarioProducto> inventarioProductos = new ArrayList<>();

    public Inventario() { }

    public Inventario(Almacenes almacen, String nombre, String descripcion) {
        this.almacen = almacen;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Almacenes getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacenes almacen) {
        this.almacen = almacen;
    }

    public List<InventarioProducto> getInventarioProductos() {
        return inventarioProductos;
    }

    public void setInventarioProductos(List<InventarioProducto> inventarioProductos) {
        this.inventarioProductos = inventarioProductos;
    }


    @Override
    public String toString() {
        return "Inventario [idinventario=" + idinventario + ", nombre=" + nombre + ", descripcion=" + descripcion
                + ", estado=" + estado + ", almacen=" + almacen + "]";
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

}
