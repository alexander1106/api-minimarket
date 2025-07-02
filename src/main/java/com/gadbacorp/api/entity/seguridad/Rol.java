package com.gadbacorp.api.entity.seguridad;


import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gadbacorp.api.entity.empleados.Usuarios;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "roles")
@SQLDelete(sql = "UPDATE roles SET estado=0 WHERE id = ?")
@Where(clause = "estado=1")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private Integer estado = 1; // 1 activo, 0 inactivo}

    @OneToMany(mappedBy = "rol")
    @JsonIgnore
    private Set<Usuarios> usuarios;


    @OneToMany(mappedBy = "rol")
    @JsonIgnore 
    private Set<RolModulo> rolModulos;
    // getters y setters
    public Set<RolModulo> getRolModulos() { return rolModulos; }
    public void setRolModulos(Set<RolModulo> rolModulos) { this.rolModulos = rolModulos; }
        public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public Set<Usuarios> getUsuarios() {
        return usuarios;
    }
    public void setUsuarios(Set<Usuarios> usuarios) {
        this.usuarios = usuarios;
    }

}

