package com.gadbacorp.api.entity.ventas;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Metodo de pago de la plataforma (Efectivo, Yape, Tarjeta...). Lo administra solo el SUPERADMIN;
 * cada sucursal activa los que usa y, si hace falta, agrega su propio dato (numero de Yape, cuenta...).
 */
@Entity
@Table(name = "metodo_pago_catalogo")
@SQLDelete(sql = "UPDATE metodo_pago_catalogo SET estado = 0 WHERE id_catalogo = ?")
@Where(clause = "estado = 1")
public class MetodoPagoCatalogo {

    public static final java.util.List<String> TIPOS =
            java.util.List.of("EFECTIVO", "DIGITAL", "TARJETA", "TRANSFERENCIA", "OTRO");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_catalogo")
    private Integer idCatalogo;

    @Column(length = 60, nullable = false)
    private String nombre;

    @Column(length = 20)
    private String tipo;

    /** true si la empresa debe indicar un dato propio al activarlo (numero de Yape, cuenta bancaria...). */
    @Column(name = "requiere_detalle")
    private Boolean requiereDetalle = false;

    private Integer estado = 1;

    public Integer getIdCatalogo() {
        return idCatalogo;
    }

    public void setIdCatalogo(Integer idCatalogo) {
        this.idCatalogo = idCatalogo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getRequiereDetalle() {
        return requiereDetalle;
    }

    public void setRequiereDetalle(Boolean requiereDetalle) {
        this.requiereDetalle = requiereDetalle;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }
}
