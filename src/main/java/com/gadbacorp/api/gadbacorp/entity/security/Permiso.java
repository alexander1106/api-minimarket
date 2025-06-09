package com.gadbacorp.api.gadbacorp.entity.security;

import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;




@Entity
@Table(name = "permisos")

public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Integer estado = 1;

@ManyToMany(mappedBy = "permisos")
@JsonBackReference // evita recursión con Rol
private Set<Rol> roles;

@ManyToOne
@JoinColumn(name = "modulo_id")
@JsonBackReference // evita recursión con Modulo
private Modulo modulo;

// Getters y setters


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



public Set<Rol> getRoles() {
    return roles;
}

public void setRoles(Set<Rol> roles) {
    this.roles = roles;
}

public Modulo getModulo() {
    return modulo;
}

public void setModulo(Modulo modulo) {
    this.modulo = modulo;
}

public Integer getEstado() {
    return estado;
}

public void setEstado(Integer estado) {
    this.estado = estado;
}


}
