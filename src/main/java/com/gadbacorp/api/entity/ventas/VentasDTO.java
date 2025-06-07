package com.gadbacorp.api.entity.ventas;

import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class VentasDTO {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idVenta;
    private double total_venta; 
    private String tipo_comprobante; 
    private String nro_comrprobante; 
    private LocalDate  fecha_venta;
    private Integer estado=1;
    private String estado_venta;
    private Integer id_cliente;

    public Integer getIdVenta() {
        return idVenta;
    }
    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }
    public double getTotal_venta() {
        return total_venta;
    }
    public void setTotal_venta(double total_venta) {
        this.total_venta = total_venta;
    }
    public String getTipo_comprobante() {
        return tipo_comprobante;
    }
    public void setTipo_comprobante(String tipo_comprobante) {
        this.tipo_comprobante = tipo_comprobante;
    }
    public String getNro_comrprobante() {
        return nro_comrprobante;
    }
    public void setNro_comrprobante(String nro_comrprobante) {
        this.nro_comrprobante = nro_comrprobante;
    }
   
    public LocalDate  getFecha_venta() {
        return fecha_venta;
    }
    public void setFecha_venta(LocalDate  fecha_venta) {
        this.fecha_venta = fecha_venta;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public Integer getId_cliente() {
        return id_cliente;
    }
    public void setId_cliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }
    public String getEstado_venta() {
        return estado_venta;
    }
    public void setEstado_venta(String estado_venta) {
        this.estado_venta = estado_venta;
    }
}
