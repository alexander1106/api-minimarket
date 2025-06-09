package com.gadbacorp.api.gadbacorp.entity.inventario;

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
    private Integer stock = 0;
    private Integer estado = 1;

    /** A qué producto corresponde este inventario */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idproducto", nullable = false)
    private Productos producto;

    /** En qué almacén está este stock */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idalmacen", nullable = false)
    private Almacenes almacen;

    /**
     * Historial de ajustes de stock (aumentos/disminuciones).
     * Se ignora en el toString/json para no recargar la respuesta.
     */
    @OneToMany(mappedBy = "inventario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<AjusteInventario> ajustes = new ArrayList<>();

    public Inventario() { }

    /** Crea un inventario nuevo con stock inicial */
    public Inventario(Productos producto, Almacenes almacen, Integer stockInicial) {
        this.producto = producto;
        this.almacen = almacen;
        this.stock    = stockInicial;
    }

    // —— Getters & Setters —— //

    public Integer getIdinventario() {
        return idinventario;
    }

    public void setIdinventario(Integer idinventario) {
        this.idinventario = idinventario;
    }

    public Integer getStock() {
        return stock;
    }

    /** Reemplaza el stock por el valor dado */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }

    public Almacenes getAlmacen() {
        return almacen;
    }

    public void setAlmacen(Almacenes almacen) {
        this.almacen = almacen;
    }

    public List<AjusteInventario> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjusteInventario> ajustes) {
        this.ajustes = ajustes;
    }

    @Override
    public String toString() {
        return "Inventario [idinventario=" + idinventario +
               ", stock=" + stock +
               ", productoId=" + (producto != null ? producto.getIdproducto() : null) +
               ", almacenId="  + (almacen  != null ? almacen.getIdalmacen()   : null) +
               "]";
    }
}
