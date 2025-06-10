package com.gadbacorp.api.entity.security;

import jakarta.persistence.*;


import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;

@Entity
@Table(name = "modulos")
@SQLDelete(sql = "UPDATE modulos SET estado=0 WHERE id = ?")
@Where(clause = "estado=1")
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String url;
    private Integer estado = 1;

@ManyToOne
@JoinColumn(name = "padre_id")
@JsonBackReference // evita la recursión del hijo al padre
private Modulo padre;

@OneToMany(mappedBy = "padre", cascade = CascadeType.ALL)
@JsonManagedReference // permite serializar la lista de hijos
private List<Modulo> hijos = new ArrayList<>();

@OneToMany(mappedBy = "modulo")
@JsonIgnore // evita serializar permisos desde el lado del módulo
private List<Permiso> permisos = new ArrayList<>();

public Modulo() {
}

public Modulo(Long id, String nombre, String url, Modulo padre, List<Modulo> hijos, List<Permiso> permisos, Integer estado) {
    this.id = id;
    this.nombre = nombre;
    this.url = url;
    this.padre = padre;
    this.hijos = hijos;
    this.permisos = permisos;
    this.estado = estado;
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

public String getUrl() {
    return url;
}

public void setUrl(String url) {
    this.url = url;
}

public Modulo getPadre() {
    return padre;
}

public void setPadre(Modulo padre) {
    this.padre = padre;
}

public List<Modulo> getHijos() {
    return hijos;
}

public void setHijos(List<Modulo> hijos) {
    this.hijos = hijos;
}

public List<Permiso> getPermisos() {
    return permisos;
}

public void setPermisos(List<Permiso> permisos) {
    this.permisos = permisos;
}

public Integer getEstado() {
    return estado;
}

public void setEstado(Integer estado) {
    this.estado = estado;
}



}
