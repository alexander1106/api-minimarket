package com.gadbacorp.api.entity.dto.ventas;

public class PagosDTO {
    private Integer idPago;
    private double monto;
    private String descripcion;
    private String fechaPago; 
    private Integer estado=1;
    private String estadoPago; 
    private String comprobante;
    private Integer idVenta;

    public PagosDTO(Integer idPago, double monto, String descripcion, String fechaPago, Integer estado,
            String estadoPago, String comprobante, Integer idVenta) {
        this.idPago = idPago;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fechaPago = fechaPago;
        this.estado = estado;
        this.estadoPago = estadoPago;
        this.comprobante = comprobante;
        this.idVenta = idVenta;
    }

    public PagosDTO() {
    }
    public Integer getIdPago() {
        return idPago;
    }
    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }
    public double getMonto() {
        return monto;
    }
    public void setMonto(double monto) {
        this.monto = monto;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String getFechaPago() {
        return fechaPago;
    }
    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public String getEstadoPago() {
        return estadoPago;
    }
    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
    public String getComprobante() {
        return comprobante;
    }
    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }
    public Integer getIdVenta() {
        return idVenta;
    }
    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }

}
