package com.gadbacorp.api.entity.inventario;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipo_producto")
@SQLDelete(sql = "UPDATE tipo_producto SET estado=0 WHERE idtipoproducto = ?")
@Where(clause="estado = 1")
public class TipoProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idtipoproducto;
    private String nombre;
    private Integer estado = 1;

    public TipoProducto(){}

    public TipoProducto(Integer id){
        this.idtipoproducto = id;
    }
    public Integer getId() {
        return idtipoproducto;
    }
    public Integer getIdtipoproducto() {
        return idtipoproducto;
    }
    public void setIdtipoproducto(Integer idtipoproducto) {
        this.idtipoproducto = idtipoproducto;
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
        return "TipoProducto [idtipoproducto=" + idtipoproducto + ", nombre=" + nombre + ", estado=" + estado + "]";
    }
    

}
