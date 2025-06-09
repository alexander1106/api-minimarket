package com.gadbacorp.api.entity.delivery;

import java.math.BigDecimal;
import java.util.Date;

public class DeliveryDTO {
private Long id;
private String direccion;
private Integer estado;
private Date fechaEntrega;
private Date fechaEnvio;
private BigDecimal costoEnvio;
private String observaciones;
private Long idVenta;
private Long idVehiculo;
private Integer idEmpleado;

// Getters y Setters

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getDireccion() { return direccion; }
public void setDireccion(String direccion) { this.direccion = direccion; }

public Integer getEstado() { return estado; }
public void setEstado(Integer estado) { this.estado = estado; }

public Date getFechaEntrega() { return fechaEntrega; }
public void setFechaEntrega(Date fechaEntrega) { this.fechaEntrega = fechaEntrega; }

public Date getFechaEnvio() { return fechaEnvio; }
public void setFechaEnvio(Date fechaEnvio) { this.fechaEnvio = fechaEnvio; }

public BigDecimal getCostoEnvio() { return costoEnvio; }
public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }

public String getObservaciones() { return observaciones; }
public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

public Long getIdVenta() { return idVenta; }
public void setIdVenta(Long idVenta) { this.idVenta = idVenta; }

public Long getIdVehiculo() { return idVehiculo; }
public void setIdVehiculo(Long idVehiculo) { this.idVehiculo = idVehiculo; }

public Integer getIdEmpleado() { return idEmpleado; }
public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }
}