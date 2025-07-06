package com.gadbacorp.api.entity.ventas;

import java.time.LocalDate;

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
@Table(name="detalles_ventas")
@SQLDelete(sql="UPDATE detalles_ventas SET estado = 0 WHERE id_detalles_venta = ?")
@Where(clause = "estado = 1")
public class DetallesVentas {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idDetallesVenta;
    private Double precioUnitario;
    private LocalDate fechaVenta;
    private Integer cantidad;
    private Double subTotal;  
    private Integer estado=1;

    @ManyToOne
    @JoinColumn(name = "idVenta")
    @JsonIgnore
    private Ventas ventas;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idproducto")
    private Productos productos;

    public Integer getIdDetallesVenta() {
        return idDetallesVenta;
    }

    public void setIdDetallesVenta(Integer idDetallesVenta) {
        this.idDetallesVenta = idDetallesVenta;
    }
  
    public LocalDate getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(LocalDate fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Ventas getVentas() {
        return ventas;
    }

    public void setVentas(Ventas ventas) {
        this.ventas = ventas;
    }

    public Productos getProductos() {
        return productos;
    }

    public void setProductos(Productos productos) {
        this.productos = productos;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

   
    
}