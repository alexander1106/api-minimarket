package com.gadbacorp.api.gadbacorp.entity.ventas;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="ventas")
@SQLDelete(sql="UPDATE ventas SET estado = 0 WHERE id_venta = ?")
@Where(clause = "estado = 1")
public class VentasDTO {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idVenta;
    private double total_venta; 
    private String tipo_comprobante; 
    private String nro_comrprobante; 
    private String fecha_venta;
    private Integer estado=1;
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
   
    public String getFecha_venta() {
        return fecha_venta;
    }
    public void setFecha_venta(String fecha_venta) {
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

    
}
