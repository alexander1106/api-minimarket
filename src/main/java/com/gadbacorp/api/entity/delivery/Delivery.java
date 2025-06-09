package com.gadbacorp.api.entity.delivery;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gadbacorp.api.entity.Vehiculo.Vehiculo;
import com.gadbacorp.api.entity.empleados.Empleado;
import com.gadbacorp.api.entity.ventas.Ventas;
import com.gadbacorp.api.enums.Estado.EstadoDelivery;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "Delivery")
public class Delivery {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String direccion;

@Enumerated(EnumType.ORDINAL)
private EstadoDelivery estado;

@Temporal(TemporalType.TIMESTAMP)
private Date fechaEntrega;

@Temporal(TemporalType.TIMESTAMP)
private Date fechaEnvio;

@Column(precision = 10, scale = 2)
private BigDecimal costoEnvio;

@Column(length = 200)
private String observaciones;

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "idVenta")
@JsonIgnoreProperties("delivery")
private Ventas venta;

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "id_vehiculo")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
private Vehiculo vehiculo;

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "id_empleado")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
private Empleado empleado;

// Getters y Setters

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public String getDireccion() { return direccion; }
public void setDireccion(String direccion) { this.direccion = direccion; }

public EstadoDelivery getEstado() { return estado; }
public void setEstado(EstadoDelivery estado) { this.estado = estado; }

public Date getFechaEntrega() { return fechaEntrega; }
public void setFechaEntrega(Date fechaEntrega) { this.fechaEntrega = fechaEntrega; }

public Date getFechaEnvio() { return fechaEnvio; }
public void setFechaEnvio(Date fechaEnvio) { this.fechaEnvio = fechaEnvio; }

public BigDecimal getCostoEnvio() { return costoEnvio; }
public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }

public String getObservaciones() { return observaciones; }
public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

public Ventas getVenta() { return venta; }
public void setVenta(Ventas venta) { this.venta = venta; }

public Vehiculo getVehiculo() { return vehiculo; }
public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

public Empleado getEmpleado() { return empleado; }
public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
}