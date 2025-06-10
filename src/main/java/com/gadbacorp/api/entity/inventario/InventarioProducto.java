package com.gadbacorp.api.entity.inventario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "inventario_producto")
@SQLDelete(sql = "UPDATE inventario_producto SET estado=0 WHERE idinventarioproducto = ?")
@Where(clause = "estado = 1")
public class InventarioProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idinventarioproducto;
    private Integer stockactual;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaingreso;
    private Integer estado = 1;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "idproducto", nullable = false)
    @JsonIgnore
    private Productos producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idinventario", nullable = false)
    @JsonIgnore
    private Inventario inventario;

    @OneToMany(
        mappedBy = "inventarioProducto",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JsonIgnore
    private List<AjusteInventario> ajustes = new ArrayList<>();

    public InventarioProducto() { }

    public InventarioProducto(Inventario inventario, Productos producto, Integer stockactual) {
        this.inventario = inventario;
        this.producto = producto;
        this.stockactual = stockactual;
    }

    public Integer getIdinventarioproducto() {
        return idinventarioproducto;
    }

    public void setIdinventarioproducto(Integer idinventarioproducto) {
        this.idinventarioproducto = idinventarioproducto;
    }

    public Integer getStockactual() {
        return stockactual;
    }

    public void setStockactual(Integer stockactual) {
        this.stockactual = stockactual;
    }

    public LocalDateTime getFechaingreso() {
        return fechaingreso;
    }

    public void setFechaingreso(LocalDateTime fechaingreso) {
        this.fechaingreso = fechaingreso;
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

    public Inventario getInventario() {
        return inventario;
    }

    public void setInventario(Inventario inventario) {
        this.inventario = inventario;
    }

    public List<AjusteInventario> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjusteInventario> ajustes) {
        this.ajustes = ajustes;
    }

    @Override
    public String toString() {
        return "InventarioProducto ["
            + "idinventarioproducto=" + idinventarioproducto
            + ", stockactual=" + stockactual
            + ", fechaingreso=" + fechaingreso
            + ", estado=" + estado
            + ", producto=" + producto
            + ", inventario=" + inventario
            + "]";
    }
}
