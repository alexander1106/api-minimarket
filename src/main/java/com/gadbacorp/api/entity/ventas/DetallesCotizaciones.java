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
@Table(name="detalles_cotizaciones")
@SQLDelete(sql="UPDATE detalles_cotizaciones SET estado = 0 WHERE id_detalles_cotizaciones = ?")
@Where(clause = "estado = 1")
public class DetallesCotizaciones {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idDetallesCotizaciones;
    private Double precioUnitario;
    private LocalDate fechaCotizacion;
    private Integer cantidad;
    private Double subTotal;  
    private Integer estado=1;

    @ManyToOne
    @JoinColumn(name = "idCotizaciones")
    @JsonIgnore
    private Cotizaciones cotizaciones;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idproducto")
    private Productos productos;

    public DetallesCotizaciones(Integer idDetallesCotizaciones) {
        this.idDetallesCotizaciones = idDetallesCotizaciones;
    }

    public DetallesCotizaciones() {
    }

    public Integer getIdDetallesCotizaciones() {
        return idDetallesCotizaciones;
    }

    public void setIdDetallesCotizaciones(Integer idDetallesCotizaciones) {
        this.idDetallesCotizaciones = idDetallesCotizaciones;
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

    public Cotizaciones getCotizaciones() {
        return cotizaciones;
    }

    public void setCotizaciones(Cotizaciones cotizaciones) {
        this.cotizaciones = cotizaciones;
    }

    public Productos getProductos() {
        return productos;
    }

    public void setProductos(Productos productos) {
        this.productos = productos;
    }

    public LocalDate getFechaCotizacion() {
        return fechaCotizacion;
    }

    public void setFechaCotizacion(LocalDate fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

}
