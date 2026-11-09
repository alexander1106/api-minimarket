package com.gadbacorp.api.entity.ventas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Contador por clave (BOLETA, FACTURA, COTIZACION) para numeracion correlativa sin duplicados. */
@Entity
@Table(name = "secuencias")
public class Secuencia {

    @Id
    @Column(length = 40)
    private String clave;

    private Long ultimo;

    public Secuencia() {
    }

    public Secuencia(String clave, Long ultimo) {
        this.clave = clave;
        this.ultimo = ultimo;
    }

    public String getClave() {
        return clave;
    }

    public Long getUltimo() {
        return ultimo;
    }

    public void setUltimo(Long ultimo) {
        this.ultimo = ultimo;
    }
}
