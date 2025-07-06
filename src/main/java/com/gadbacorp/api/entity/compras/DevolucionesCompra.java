package com.gadbacorp.api.entity.compras;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "devolucionesCompras")
@SQLDelete(sql = "UPDATE devolucionesCompras SET estado=0 WHERE id_devolucionCompras = ?")
@Where(clause = "estado=1")
public class DevolucionesCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devolucionCompras")
    private Integer idDevolucionCompra;

    private Integer estado = 1;
    private LocalDateTime fechaDevolucion;
    private BigDecimal montoDerivativo;
    private String motivoDevolucion;
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "id_compra")
    private Compras compra;

    @OneToMany(mappedBy = "devolucionCompra", cascade = CascadeType.ALL)
    private List<DetallesDevolucionCompra> detalles;

    // Getters y Setters
    public Integer getIdDevolucionCompra() {
        return idDevolucionCompra;
    }

    public void setIdDevolucionCompra(Integer idDevolucionCompra) {
        this.idDevolucionCompra = idDevolucionCompra;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public BigDecimal getMontoDerivativo() {
        return montoDerivativo;
    }

    public void setMontoDerivativo(BigDecimal montoDerivativo) {
        this.montoDerivativo = montoDerivativo;
    }

    public String getMotivoDevolucion() {
        return motivoDevolucion;
    }

    public void setMotivoDevolucion(String motivoDevolucion) {
        this.motivoDevolucion = motivoDevolucion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Compras getCompra() {
        return compra;
    }

    public void setCompra(Compras compra) {
        this.compra = compra;
    }

    public List<DetallesDevolucionCompra> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallesDevolucionCompra> detalles) {
        this.detalles = detalles;
    }
}