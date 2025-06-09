package com.gadbacorp.api.entity.delivery;

public class VehiculoDTO {
     private Long id;
    private String placa;
    private String modelo;
    private String marca;
    private String anio;
    private String color;
    private Double capacidadKg;
    private String estado;
    private String observaciones;

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
}


