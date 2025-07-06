package com.gadbacorp.api.entity.caja;

import java.util.Optional;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gadbacorp.api.entity.ventas.MetodosPago;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;



@Entity
@Table(name = "saldoMetodoPago")
@SQLDelete(sql="UPDATE saldoMetodoPago SET estado = 0 WHERE id_saldo_metodo_pago = ?")
@Where(clause = "estado = 1")
public class SaldoMetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSaldoMetodoPago;
    private Integer estado=1;
    private Double saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_apertura_caja")
    @JsonIgnore // 👈 Evita que se serialice
    private AperturaCaja aperturaCaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // 👈 Ignora el proxy
    private MetodosPago metodoPago;

    public Integer getIdSaldoMetodoPago() {
        return idSaldoMetodoPago;
    }

    public void setIdSaldoMetodoPago(Integer idSaldoMetodoPago) {
        this.idSaldoMetodoPago = idSaldoMetodoPago;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public AperturaCaja getAperturaCaja() {
        return aperturaCaja;
    }

    public void setAperturaCaja(AperturaCaja aperturaCaja) {
        this.aperturaCaja = aperturaCaja;
    }

    public MetodosPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodosPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Optional<SaldoMetodoPago> findByAperturaCajaAndMetodoPago(AperturaCaja aperturaCaja2, MetodosPago metodo) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByAperturaCajaAndMetodoPago'");
    }

    
    
}
