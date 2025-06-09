package com.gadbacorp.api.gadbacorp.entity.ventas;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
    private double total_venta; 
    private String tipo_comprobante; 
    private String nro_comrprobante; 
    private String fecha_venta;
    private Integer estado=1;

    // RELACIÓN CON CLIENTES
    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id_cliente")
    @JsonIgnore // evita ciclos infinitos si usas JSON
    private Clientes cliente;

    @OneToMany(mappedBy = "ventas", cascade = CascadeType.ALL)
    private List<DetallesVentas> detallesVentas;
    

    @OneToMany(mappedBy = "ventas", cascade = CascadeType.ALL)
    private List<Pagos> pagos;


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


    public Ventas() {
    }


    public Ventas(Integer idVenta) {
        this.idVenta = idVenta;
    }




    
}
