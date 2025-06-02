package com.gadbacorp.api.entity.dto.delivery;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

public class DeliveryDTO {


private Long id;

@NotBlank(message = "La dirección es obligatoria")
private String direccion;

@NotNull(message = "El estado es obligatorio")
private Integer estado;

@NotNull(message = "La fecha de entrega es obligatoria")
private Date fechaEntrega;

private Date fechaEnvio;

@DecimalMin(value = "0.0", inclusive = true, message = "El costo de envío debe ser positivo")
private BigDecimal costoEnvio;

@Size(max = 200, message = "Las observaciones no deben superar los 200 caracteres")
private String observaciones;

@NotNull(message = "El ID de venta es obligatorio")
private Long idVenta;

// Getters y Setters

public Long getId() {
    return id;
}

public void setId(Long id) {
    this.id = id;
}

public String getDireccion() {
    return direccion;
}

public void setDireccion(String direccion) {
    this.direccion = direccion;
}

public Integer getEstado() {
    return estado;
}

public void setEstado(Integer estado) {
    this.estado = estado;
}

public Date getFechaEntrega() {
    return fechaEntrega;
}

public void setFechaEntrega(Date fechaEntrega) {
    this.fechaEntrega = fechaEntrega;
}

public Date getFechaEnvio() {
    return fechaEnvio;
}

public void setFechaEnvio(Date fechaEnvio) {
    this.fechaEnvio = fechaEnvio;
}

public BigDecimal getCostoEnvio() {
    return costoEnvio;
}

public void setCostoEnvio(BigDecimal costoEnvio) {
    this.costoEnvio = costoEnvio;
}

public String getObservaciones() {
    return observaciones;
}

public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
}

public Long getIdVenta() {
    return idVenta;
}

public void setIdVenta(Long idVenta) {
    this.idVenta = idVenta;
}
}