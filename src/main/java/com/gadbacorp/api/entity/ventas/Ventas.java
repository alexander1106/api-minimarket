package com.gadbacorp.api.entity.ventas;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="ventas")
public class Ventas {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_venta")
    private Integer idVenta;
    private String descripcion;
    @Column(name="total_venta")
    private double totalVenta; 
    @Column(name="tipo_comprobante")
    private String tipoComprobante; 
    @Column(name="nro_comrprobante")
    private String nroComprobante; 
    @Column(name="estado_venta")
    private String estadoVenta; 
    @Column(name="fecha_venta")
    private String fechaVenta;
    private Integer estado=1;

        // RELACIÓN CON CLIENTES
    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente")
    @JsonIgnore // evita ciclos infinitos si usas JSON
    private Clientes cliente;


    public Integer getIdVenta() {
        return idVenta;
    }
    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }
    public double getTotalVenta() {
        return totalVenta;
    }
    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }
    public String getTipoComprobante() {
        return tipoComprobante;
    }
    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }
    public String getNroComprobante() {
        return nroComprobante;
    }
    public void setNroComprobante(String nroComprobante) {
        this.nroComprobante = nroComprobante;
    }
    public String getEstadoVenta() {
        return estadoVenta;
    }
    public void setEstadoVenta(String estadoVenta) {
        this.estadoVenta = estadoVenta;
    }
    public String getFechaVenta() {
        return fechaVenta;
    }
    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Clientes getCliente() {
        return cliente;
    }
    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    } 

    

}
