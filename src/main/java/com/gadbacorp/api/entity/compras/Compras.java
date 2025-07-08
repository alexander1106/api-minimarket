package com.gadbacorp.api.entity.compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gadbacorp.api.entity.administrable.Sucursales;
import com.gadbacorp.api.entity.ventas.MetodosPago;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

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

    @ManyToOne
    @JoinColumn(name = "id_sucursal")
    private Sucursales sucursal;

    private BigDecimal total;
    private String descripcion;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;
    private Integer estado = 1;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<DetallesCompras> detalles;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<DevolucionesCompra> devoluciones;

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

    public Sucursales getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
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

    // Getters y Setters
    
    
}