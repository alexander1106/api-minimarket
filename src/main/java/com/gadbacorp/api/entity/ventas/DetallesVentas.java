package com.gadbacorp.api.entity.ventas;

import com.gadbacorp.api.entity.inventario.Productos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="detalles_ventas")
public class DetallesVentas {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_detalles_venta")
    private Integer idDetallesVenta;

    private double cantidad; 
    private double precio;
    private Integer estado=1;

    @ManyToOne
    @JoinColumn(name = "id_venta")
    private Ventas ventas;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Productos productos;

}
