package com.gadbacorp.api.entity.compras;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.gadbacorp.api.entity.ventas.MetodosPago;

@Entity
@Table(name = "Compras")
public class Compras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_compra")
    private Integer idCompra;

    @ManyToOne
    @JoinColumn(name = "Id_Proveedor")
    private Proveedores proveedor;

    @ManyToOne
    @JoinColumn(name = "idMetodoPago")
    private MetodosPago metodoPago;

    private BigDecimal total;

    private String descripcion;

    @Column(name = "fecha_compra")
    private LocalDateTime fechaCompra;

    private Integer estado;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<DetallesCompras> detalles;

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

    @Override
    public String toString() {
        return "Compra [idCompra=" + idCompra + ", proveedor=" + proveedor + ", metodoPago=" + metodoPago + ", total="
                + total + ", descripcion=" + descripcion + ", fechaCompra=" + fechaCompra + ", estado=" + estado
                + ", detalles=" + detalles + "]";
    }

   
    
}