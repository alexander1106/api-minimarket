package com.gadbacorp.api.entity.notificaciones;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * Aviso para un usuario concreto. Un hecho que interesa a varios usuarios genera una fila por cada uno,
 * asi cada quien marca como leidas las suyas sin afectar a los demas.
 */
@Entity
@Table(name = "notificaciones", indexes = {
        @Index(name = "idx_notif_usuario", columnList = "id_usuario,leida,fecha")})
public class Notificacion {

    public static final String STOCK_BAJO = "STOCK_BAJO";
    public static final String POR_VENCER = "POR_VENCER";
    public static final String VENCIDO = "VENCIDO";
    public static final String SUSCRIPCION = "SUSCRIPCION";
    public static final String SISTEMA = "SISTEMA";

    public static final String INFO = "INFO";
    public static final String ADVERTENCIA = "ADVERTENCIA";
    public static final String CRITICA = "CRITICA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(length = 20)
    private String tipo;

    /** INFO, ADVERTENCIA o CRITICA: sirve para el color del aviso. */
    @Column(length = 12)
    private String severidad = INFO;

    @Column(length = 120)
    private String titulo;

    @Column(length = 400)
    private String mensaje;

    /** Ruta de la pantalla del frontend a la que lleva el aviso. */
    @Column(length = 120)
    private String ruta;

    /** Identifica el hecho para no repetir el mismo aviso mientras siga sin leerse. */
    @Column(length = 120)
    private String clave;

    private LocalDateTime fecha = LocalDateTime.now();

    private Boolean leida = false;

    @Column(name = "fecha_lectura")
    private LocalDateTime fechaLectura;

    public Long getIdNotificacion() {
        return idNotificacion;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getSeveridad() {
        return severidad;
    }

    public void setSeveridad(String severidad) {
        this.severidad = severidad;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Boolean getLeida() {
        return leida;
    }

    public void setLeida(Boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFechaLectura() {
        return fechaLectura;
    }

    public void setFechaLectura(LocalDateTime fechaLectura) {
        this.fechaLectura = fechaLectura;
    }
}
