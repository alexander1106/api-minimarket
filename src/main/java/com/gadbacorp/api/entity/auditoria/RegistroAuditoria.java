package com.gadbacorp.api.entity.auditoria;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/** Una accion hecha en el sistema: quien, cuando, sobre que, desde donde y con que resultado. Solo se escribe, nunca se edita. */
@Entity
@Table(name = "auditoria", indexes = {
        @Index(name = "idx_audit_fecha", columnList = "fecha"),
        @Index(name = "idx_audit_usuario", columnList = "username,fecha")})
public class RegistroAuditoria {

    public static final String CREAR = "CREAR";
    public static final String EDITAR = "EDITAR";
    public static final String ELIMINAR = "ELIMINAR";
    public static final String INICIO_SESION = "INICIO_SESION";
    public static final String INICIO_SESION_FALLIDO = "INICIO_SESION_FALLIDO";
    public static final String OTRO = "OTRO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha = LocalDateTime.now();

    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(length = 80)
    private String username;

    @Column(length = 30)
    private String rol;

    @Column(name = "id_empresa")
    private Integer idEmpresa;

    @Column(length = 160)
    private String empresa;

    @Column(length = 24)
    private String accion;

    @Column(length = 30)
    private String modulo;

    /** Primer tramo de la ruta (productos, ventas, usuarios...). */
    @Column(length = 60)
    private String recurso;

    @Column(name = "id_registro", length = 40)
    private String idRegistro;

    @Column(length = 8)
    private String metodo;

    @Column(length = 300)
    private String ruta;

    @Column(name = "estado_http")
    private Integer estadoHttp;

    private Boolean exito;

    @Column(length = 45)
    private String ip;

    @Column(name = "user_agent", length = 200)
    private String userAgent;

    @Column(name = "duracion_ms")
    private Long duracionMs;

    /** Frase lista para mostrar: "Alez creó un registro en productos (#12)". */
    @Column(length = 300)
    private String descripcion;

    /** Datos que envio el usuario (sin contrasenas ni tokens) y mensaje de error si fallo. */
    @Column(columnDefinition = "TEXT")
    private String detalle;

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getRecurso() {
        return recurso;
    }

    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }

    public String getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(String idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Integer getEstadoHttp() {
        return estadoHttp;
    }

    public void setEstadoHttp(Integer estadoHttp) {
        this.estadoHttp = estadoHttp;
    }

    public Boolean getExito() {
        return exito;
    }

    public void setExito(Boolean exito) {
        this.exito = exito;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Long getDuracionMs() {
        return duracionMs;
    }

    public void setDuracionMs(Long duracionMs) {
        this.duracionMs = duracionMs;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
}
