package com.gadbacorp.api.entity.Vehiculo;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "vehiculos")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String placa;

    @Column(nullable = false, length = 50)
    private String modelo;

    @Column(nullable = false, length = 20)
    private String marca;

    @Column(nullable = false, length = 10)
    private String anio;

    @Column(nullable = false, length = 20)
    private String color;

    @Column(name = "capacidad_kg", nullable = false)
    private Double capacidadKg;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(length = 200)
    private String observaciones;

    // Constructor vacío
    public Vehiculo() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getAnio() { return anio; }
    public void setAnio(String anio) { this.anio = anio; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Double getCapacidadKg() { return capacidadKg; }
    public void setCapacidadKg(Double capacidadKg) { this.capacidadKg = capacidadKg; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    // equals() y hashCode() (basados en id)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehiculo)) return false;
        Vehiculo that = (Vehiculo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // toString()
    @Override
    public String toString() {
        return "Vehiculo{" +
                "id=" + id +
                ", placa='" + placa + '\'' +
                ", modelo='" + modelo + '\'' +
                ", marca='" + marca + '\'' +
                ", anio='" + anio + '\'' +
                ", color='" + color + '\'' +
                ", capacidadKg=" + capacidadKg +
                ", estado='" + estado + '\'' +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
