package com.gadbacorp.api.entity.delivery;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "estado_movimiento")
@SQLDelete(sql = "UPDATE estado_movimiento SET estado=0 WHERE idestadomovimiento = ?")
@Where(clause = "estado=1")
public class EstadoMovimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idestadomovimiento;
    private String Nombre;
    private Integer estado = 1;
    
    public EstadoMovimiento() {
    }

    public EstadoMovimiento(Integer idestadomovimiento, String nombre, Integer estado) {
        this.idestadomovimiento = idestadomovimiento;
        Nombre = nombre;
        this.estado = estado;
    }

    public Integer getIdestadomovimiento() {
        return idestadomovimiento;
    }

    public void setIdestadomovimiento(Integer idestadomovimiento) {
        this.idestadomovimiento = idestadomovimiento;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "EstadoMovimiento [idestadomovimiento=" + idestadomovimiento + ", Nombre=" + Nombre + ", estado="
                + estado + "]";
    }

}
