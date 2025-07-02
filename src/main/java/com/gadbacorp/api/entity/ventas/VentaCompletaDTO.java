package com.gadbacorp.api.entity.ventas;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class VentaCompletaDTO {
    
    private double total_venta; 
    private String tipo_comprobante; 
    private LocalDate  fecha_venta;
    private Integer estado=1;
    private String estado_venta;
    private Integer id_cliente;
    private List<DetallesVentasDTO> detalles;

    private Integer idSucursal;
    

    private String estadoPago;
    private LocalDate fechaPago; 
    private  Double montoPagado;
    private String observaciones;
    private Integer id_metodo_pago;

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

    

    public LocalDate getFecha_venta() {
        return fecha_venta;
    }
    public void setFecha_venta(LocalDate fecha_venta) {
        this.fecha_venta = fecha_venta;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public String getEstado_venta() {
        return estado_venta;
    }
    public void setEstado_venta(String estado_venta) {
        this.estado_venta = estado_venta;
    }
    public Integer getId_cliente() {
        return id_cliente;
    }
    public void setId_cliente(Integer id_cliente) {
        this.id_cliente = id_cliente;
    }
   
    public String getEstadoPago() {
        return estadoPago;
    }
    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public Integer getId_metodo_pago() {
        return id_metodo_pago;
    }
    public void setId_metodo_pago(Integer id_metodo_pago) {
        this.id_metodo_pago = id_metodo_pago;
    }
    public LocalDate getFechaPago() {
        return fechaPago;
    }
    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }
    public Double getMontoPagado() {
        return montoPagado;
    }
    public void setMontoPagado(Double montoPagado) {
        this.montoPagado = montoPagado;
    }
    public List<DetallesVentasDTO> getDetalles() {
        return detalles;
    }
    public void setDetalles(List<DetallesVentasDTO> detalles) {
        this.detalles = detalles;
    }
    public Integer getIdSucursal() {
        return idSucursal;
    }
    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }
 
    
}
