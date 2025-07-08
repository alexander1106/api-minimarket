package com.gadbacorp.api.entity.seguridad;



import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "modulos")
@SQLDelete(sql = "UPDATE modulos SET estado=0 WHERE id_modulo = ?")
@Where(clause = "estado=1")
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idModulo;
    private String nombre;
    private Integer estado = 1;

    @OneToMany(mappedBy = "modulo")
    @JsonIgnore 
    private Set<RolModulo> rolModulos;

    public Set<RolModulo> getRolModulos() { return rolModulos; }
    public void setRolModulos(Set<RolModulo> rolModulos) { this.rolModulos = rolModulos; }


    public Integer getIdModulo() {
        return idModulo;
    }
    public void setIdModulo(Integer idModulo) {
        this.idModulo = idModulo;
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

}