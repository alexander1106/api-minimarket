package com.gadbacorp.api.entity.inventario;

import java.time.LocalDate;

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
@Table(name = "almacen_producto")
@SQLDelete(sql = "UPDATE almacen_producto SET estado=0 WHERE idalmacenproducto = ?")
@Where(clause="estado = 1")
public class AlmacenProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idalmacenproducto;
    private Integer stock;
    private LocalDate fechaIngreso; 
    private Integer estado = 1;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "id_almacen")
    @JsonIgnoreProperties({"almacenProductos"})
    private Almacenes almacen;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    @JsonIgnoreProperties({"almacenProductos"})
    private Productos producto;

    public Integer getIdalmacenproducto() {
        return idalmacenproducto;
    }

    public void setIdalmacenproducto(Integer idalmacenproducto) {
        this.idalmacenproducto = idalmacenproducto;
    }

    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
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

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }

    @Override
    public String toString() {
        return "AlmacenProducto [idalmacenproducto=" + idalmacenproducto + ", stock=" + stock + ", fechaIngreso="
                + fechaIngreso + ", estado=" + estado + ", almacen=" + almacen + ", producto=" + producto + "]";
    }
    
    

    

}
