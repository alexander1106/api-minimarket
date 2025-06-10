package com.gadbacorp.api.entity.ventas;


import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gadbacorp.api.entity.inventario.Productos;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="detalles_devoluciones")
@SQLDelete(sql="UPDATE detalles_devoluciones SET estado = 0 WHERE id_detalles_devoluciones = ?")
@Where(clause = "estado = 1")
public class DetallesDevolucion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idDetallesDevoluciones; 
    private double pecioUnitario;
    private Integer cantidad;
    private double subTotal;  
    private Integer estado=1;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idproducto")
    private Productos productos;
    
    @ManyToOne
    @JoinColumn(name = "idDevolucion")
    @JsonIgnore
    private Devoluciones devoluciones;

    public Integer getIdDetallesDevoluciones() {
        return idDetallesDevoluciones;
    }

    public void setIdDetallesDevoluciones(Integer idDetallesDevoluciones) {
        this.idDetallesDevoluciones = idDetallesDevoluciones;
    }

    public double getPecioUnitario() {
        return pecioUnitario;
    }

    public void setPecioUnitario(double pecioUnitario) {
        this.pecioUnitario = pecioUnitario;
    }


    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Productos getProductos() {
        return productos;
    }

    public void setProductos(Productos productos) {
        this.productos = productos;
    }

    public Devoluciones getDevoluciones() {
        return devoluciones;
    }

    public void setDevoluciones(Devoluciones devoluciones) {
        this.devoluciones = devoluciones;
    }
}
