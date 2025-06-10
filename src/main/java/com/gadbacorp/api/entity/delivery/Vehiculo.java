package com.gadbacorp.api.entity.delivery;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehiculos")
@SQLDelete(sql = "UPDATE vehiculos SET estado=0 WHERE idvehiculo = ?")
@Where(clause = "estado=1")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idvehiculo;
    private String placa;
    private String modelo;
    private String marca;
    private String anio;
    private String color;
    private String observaciones;
    private Integer estado = 1;

    

    public Vehiculo() {
    }

    public Vehiculo(Integer idvehiculo, String placa, String modelo, String marca, String anio, String color,
            String observaciones, Integer estado) {
        this.idvehiculo = idvehiculo;
        this.placa = placa;
        this.modelo = modelo;
        this.marca = marca;
        this.anio = anio;
        this.color = color;
        this.observaciones = observaciones;
        this.estado = estado;
    }

    public Integer getIdvehiculo() {
        return idvehiculo;
    }
    public void setIdvehiculo(Integer idvehiculo) {
        this.idvehiculo = idvehiculo;
    }
    public String getPlaca() {
        return placa;
    }
    public void setPlaca(String placa) {
        this.placa = placa;
    }
    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }
    public String getMarca() {
        return marca;
    }
    public void setMarca(String marca) {
        this.marca = marca;
    }
    public String getAnio() {
        return anio;
    }
    public void setAnio(String anio) {
        this.anio = anio;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "Vehiculo [idvehiculo=" + idvehiculo + ", placa=" + placa + ", modelo=" + modelo + ", marca=" + marca
                + ", anio=" + anio + ", color=" + color + ", observaciones=" + observaciones + ", estado=" + estado
                + "]";
    }
    

}
