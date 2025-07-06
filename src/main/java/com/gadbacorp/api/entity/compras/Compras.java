package com.gadbacorp.api.entity.compras;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gadbacorp.api.entity.ventas.MetodosPago;

@Entity
@Table(name = "Compras")
@SQLDelete(sql = "UPDATE Compras SET estado=0 WHERE id_compra = ?")
@Where(clause = "estado=1")
public class Compras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private Integer idCompra;

    @ManyToOne
    @JoinColumn(name = "Id_Proveedor")
    private Proveedores proveedor;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago")
    private MetodosPago metodoPago;

    private BigDecimal total;
    
    @Column(name = "precio_compra")
    private BigDecimal precioCompra;
    
    @Column(name = "precio_venta")
    private BigDecimal precioVenta;
    
    private String descripcion;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;
    private Integer estado = 1;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DetallesCompras> detalles;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<DevolucionesCompra> devoluciones;

    // Getters y Setters
    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Proveedores getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedores proveedor) {
        this.proveedor = proveedor;
    }

    public MetodosPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodosPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public List<DetallesCompras> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallesCompras> detalles) {
        this.detalles = detalles;
    }

    public List<DevolucionesCompra> getDevoluciones() {
        return devoluciones;
    }

    public void setDevoluciones(List<DevolucionesCompra> devoluciones) {
        this.devoluciones = devoluciones;
    }
}