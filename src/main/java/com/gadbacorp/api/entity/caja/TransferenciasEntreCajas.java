package com.gadbacorp.api.entity.caja;

import java.util.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="transeferencias_entre_cajas")
@SQLDelete(sql="UPDATE transeferencias_entre_cajas SET estado = 0 WHERE id_transferencia_entre_cajas = ?")
@Where(clause = "estado = 1")
public class TransferenciasEntreCajas {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idTransferenciaEntreCajas; 
    private Double monto;
    private Date fecha;
    private String motivo; 
    private Integer estado=1;
    private String observaciones; 
    
   @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_caja_origen")
    private Caja cajaOrigen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_caja_destino")
    private Caja cajaDestino;


    public Integer getIdTransferenciaEntreCajas() {
        return idTransferenciaEntreCajas;
    }

    public void setIdTransferenciaEntreCajas(Integer idTransferenciaEntreCajas) {
        this.idTransferenciaEntreCajas = idTransferenciaEntreCajas;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Caja getCajaOrigen() {
        return cajaOrigen;
    }

    public void setCajaOrigen(Caja cajaOrigen) {
        this.cajaOrigen = cajaOrigen;
    }

    public Caja getCajaDestino() {
        return cajaDestino;
    }

    public void setCajaDestino(Caja cajaDestino) {
        this.cajaDestino = cajaDestino;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    
}
