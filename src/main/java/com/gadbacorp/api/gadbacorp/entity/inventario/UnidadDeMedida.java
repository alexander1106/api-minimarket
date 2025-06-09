package com.gadbacorp.api.gadbacorp.entity.inventario;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "unidad_medida")
@SQLDelete(sql = "UPDATE unidad_medida SET estado=0 WHERE idunidadmedida = ?")
@Where(clause="estado = 1")
public class UnidadDeMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id_unidad_medida")
    private Integer idunidadmedida;
    private String nombre;
    private Integer estado = 1;

    public UnidadDeMedida(){}

    public UnidadDeMedida(Integer id){
        this.idunidadmedida = id;
    }

    public Integer getIdunidadmedida() {
        return idunidadmedida;
    }
    public void setIdunidadmedida(Integer idunidadmedida) {
        this.idunidadmedida = idunidadmedida;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    @Override
    public String toString() {
        return "UnidadDeMedida [idunidadmedida=" + idunidadmedida + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
    
}
