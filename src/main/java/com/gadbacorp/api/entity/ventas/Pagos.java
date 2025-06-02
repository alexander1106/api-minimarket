package com.gadbacorp.api.entity.ventas;
import java.util.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
@Table(name="pagos")
@SQLDelete(sql="UPDATE pagos SET estado = 0 WHERE id_pago = ?")
@Where(clause = "estado = 1")
public class Pagos {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_pago")
    private Integer idPago;
    private String observaciones;
    private Date fechaPago; 
    private Double montoPagado;
    private String referenciaPago;
    private String estadoPago;
    private Integer estado=1;

    @ManyToOne
    @JoinColumn(name = "idVenta", referencedColumnName = "idVenta")
    @JsonIgnore // evita ciclos infinitos si usas JSON
    private Ventas ventas;

     @ManyToOne
    @JoinColumn(name = "id_metodo_pago", referencedColumnName = "id_metodo_pago")
    @JsonIgnore // evita ciclos infinitos si usas JSON
    private MetodosPago metodosPago;

     public Integer getIdPago() {
         return idPago;
     }

     public void setIdPago(Integer idPago) {
         this.idPago = idPago;
     }

     public String getObservaciones() {
         return observaciones;
     }

     public void setObservaciones(String observaciones) {
         this.observaciones = observaciones;
     }

     public Date getFechaPago() {
         return fechaPago;
     }

     public void setFechaPago(Date fechaPago) {
         this.fechaPago = fechaPago;
     }

     public Double getMontoPagado() {
         return montoPagado;
     }

     public void setMontoPagado(Double montoPagado) {
         this.montoPagado = montoPagado;
     }

     public String getReferenciaPago() {
         return referenciaPago;
     }

     public void setReferenciaPago(String referenciaPago) {
         this.referenciaPago = referenciaPago;
     }

     public String getEstadoPago() {
         return estadoPago;
     }

     public void setEstadoPago(String estadoPago) {
         this.estadoPago = estadoPago;
     }

     public Integer getEstado() {
         return estado;
     }

     public void setEstado(Integer estado) {
         this.estado = estado;
     }

     public Ventas getVentas() {
         return ventas;
     }

     public void setVentas(Ventas ventas) {
         this.ventas = ventas;
     }

     public MetodosPago getMetodosPago() {
         return metodosPago;
     }

     public void setMetodosPago(MetodosPago metodosPago) {
         this.metodosPago = metodosPago;
     }

  

}
