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
@Table(name="ventas")
@SQLDelete(sql="UPDATE ventas SET estado = 0 WHERE id_venta = ?")
@Where(clause = "estado = 1")
public class Ventas {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idVenta;
    private Double total_venta; 
    /** Quien cobro la venta y el turno de caja sobre el que se cobro (null si no se uso caja). */
    @jakarta.persistence.Column(name = "id_vendedor")
    private Integer idVendedor;
    @jakarta.persistence.Column(length = 80)
    private String vendedor;
    @jakarta.persistence.Column(name = "id_apertura_caja")
    private Integer idAperturaCaja;
    /** Desglose de IGV: base de las lineas afectas, importe de las inafectas e IGV (el total ya lo incluye). */
    @jakarta.persistence.Column(name = "op_gravada", precision = 12, scale = 2)
    private java.math.BigDecimal opGravada;
    @jakarta.persistence.Column(name = "op_inafecta", precision = 12, scale = 2)
    private java.math.BigDecimal opInafecta;
    @jakarta.persistence.Column(precision = 12, scale = 2)
    private java.math.BigDecimal igv;
    private String tipo_comprobante; 
    private String nro_comrprobante; 
    private LocalDate  fecha_venta;
    private Integer estado=1;
    private String estado_venta;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente")
    private Clientes cliente;



    @OneToMany(mappedBy = "ventas", cascade = CascadeType.ALL)
    private List<DetallesVentas> detallesVentas;
    
    @OneToMany(mappedBy = "ventas", cascade = CascadeType.ALL)
    private List<Pagos> pagos;

    @OneToMany(mappedBy = "ventas", cascade = CascadeType.ALL)
    private List<Devoluciones> devoluciones;

     public Ventas(Integer idVenta) {
        this.idVenta = idVenta;
    }

    public Ventas() {
    }

    public Integer getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
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

    public Clientes getCliente() {
        return cliente;
    }

    public void setCliente(Clientes cliente) {
        this.cliente = cliente;
    }


    public List<DetallesVentas> getDetallesVentas() {
        return detallesVentas;
    }

    public void setDetallesVentas(List<DetallesVentas> detallesVentas) {
        this.detallesVentas = detallesVentas;
    }

    public List<Pagos> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pagos> pagos) {
        this.pagos = pagos;
    }

    public List<Devoluciones> getDevoluciones() {
        return devoluciones;
    }

    public void setDevoluciones(List<Devoluciones> devoluciones) {
        this.devoluciones = devoluciones;
    }

    public Double getTotal_venta() {
        return total_venta;
    }

    public void setTotal_venta(Double total_venta) {
        this.total_venta = total_venta;
    }

    public java.math.BigDecimal getOpGravada() {
        return opGravada;
    }

    public void setOpGravada(java.math.BigDecimal opGravada) {
        this.opGravada = opGravada;
    }

    public java.math.BigDecimal getOpInafecta() {
        return opInafecta;
    }

    public void setOpInafecta(java.math.BigDecimal opInafecta) {
        this.opInafecta = opInafecta;
    }

    public java.math.BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(java.math.BigDecimal igv) {
        this.igv = igv;
    }

    public Integer getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public Integer getIdAperturaCaja() {
        return idAperturaCaja;
    }

    public void setIdAperturaCaja(Integer idAperturaCaja) {
        this.idAperturaCaja = idAperturaCaja;
    }
}
