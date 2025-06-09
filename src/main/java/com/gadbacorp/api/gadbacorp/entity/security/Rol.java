package com.gadbacorp.api.gadbacorp.entity.security;

import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import jakarta.persistence.*;


@Entity
@Table(name = "roles")
@SQLDelete(sql = "UPDATE roles SET estado=0 WHERE id = ?")
@Where(clause = "estado=1")

public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private Integer estado = 1; // 1 activo, 0 inactivo


@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "rol_permiso",
    joinColumns = @JoinColumn(name = "rol_id"),
    inverseJoinColumns = @JoinColumn(name = "permiso_id")
)
@JsonIgnore // evita que el JSON serialice esto desde el lado del rol
private Set<Permiso> permisos = new HashSet<>();





public Rol() {
}


public Rol(Long id, String nombre, Integer estado, Set<Permiso> permisos) {
    this.id = id;
    this.nombre = nombre;
    this.estado = estado;
    this.permisos = permisos;
}



public Long getId() {
    return id;
}


public void setId(Long id) {
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


public Set<Permiso> getPermisos() {
    return permisos;
}


public void setPermisos(Set<Permiso> permisos) {
    this.permisos = permisos;
}


}
