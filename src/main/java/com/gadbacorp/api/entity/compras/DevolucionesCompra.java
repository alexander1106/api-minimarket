package com.gadbacorp.api.entity.compras;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.gadbacorp.api.entity.inventario.Productos;
import com.gadbacorp.api.entity.ventas.MetodosPago;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "devoluciones_compra")
@SQLDelete(sql = "UPDATE devoluciones_compra SET estado = 0 WHERE Id_devolucion = ?")
@Where(clause = "estado = 1")
public class DevolucionesCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_devolucion")
    private Integer idDevolucion;

    @ManyToOne
    @JoinColumn(name = "Id_compra")
    private Compras compra;

    @ManyToOne
    @JoinColumn(name = "id_producto")
    private Productos producto;

    @ManyToOne
    @JoinColumn(name = "idMetodoPago")
    private MetodosPago metodoPago;

    @Column(name = "cantidad_devuelta")
    private Integer cantidadDevuelta;

    private String motivo;

    @Column(name = "fecha_devolucion")
    private LocalDateTime fechaDevolucion;

    private Integer estado = 1;

    // Getters and Setters
    public Integer getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(Integer idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public Compras getCompra() {
        return compra;
    }

    public void setCompra(Compras compra) {
        this.compra = compra;
    }

    public Productos getProducto() {
        return producto;
    }

    public void setProducto(Productos producto) {
        this.producto = producto;
    }

    public MetodosPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodosPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Integer getCantidadDevuelta() {
        return cantidadDevuelta;
    }

    public void setCantidadDevuelta(Integer cantidadDevuelta) {
        this.cantidadDevuelta = cantidadDevuelta;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}