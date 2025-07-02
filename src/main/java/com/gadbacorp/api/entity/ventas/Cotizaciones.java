package com.gadbacorp.api.entity.ventas;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name="cotizaciones")
@SQLDelete(sql="UPDATE cotizaciones SET estado = 0 WHERE id_cotizaciones = ?")
@Where(clause = "estado = 1")
public class Cotizaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCotizaciones;
    private LocalDate fechaCotizacion;
    private LocalDate fechaVencimiento;
    private String numeroCotizacion;
    private String estadoCotizacion;
    private double totalCotizacion;
    private Integer estado=1;
    
    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente")
    private Clientes cliente;


    @OneToMany(mappedBy = "cotizaciones", cascade = CascadeType.ALL)
    private List<DetallesCotizaciones> detallesCotizaciones;
    
    public Integer getIdCotizaciones() {
        return idCotizaciones;
    }

    public void setIdCotizaciones(Integer idCotizaciones) {
        this.idCotizaciones = idCotizaciones;
    }

    public String getNumeroCotizacion() {
        return numeroCotizacion;
    }

    public void setNumeroCotizacion(String numeroCotizacion) {
        this.numeroCotizacion = numeroCotizacion;
    }

    public String getEstadoCotizacion() {
        return estadoCotizacion;
    }

    public void setEstadoCotizacion(String estadoCotizacion) {
        this.estadoCotizacion = estadoCotizacion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }

    public LocalDate getFechaCotizacion() {
        return fechaCotizacion;
    }

    public void setFechaCotizacion(LocalDate fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }

    public Cotizaciones() {
    }

    public List<DetallesCotizaciones> getDetallesCotizaciones() {
        return detallesCotizaciones;
    }

    public void setDetallesCotizaciones(List<DetallesCotizaciones> detallesCotizaciones) {
        this.detallesCotizaciones = detallesCotizaciones;
    }

    public double getTotalCotizacion() {
        return totalCotizacion;
    }

    public void setTotalCotizacion(double totalCotizacion) {
        this.totalCotizacion = totalCotizacion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }
}
