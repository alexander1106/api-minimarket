package com.gadbacorp.api.entity.inventario;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "categorias")
@SQLDelete(sql = "UPDATE categorias SET estado=0 WHERE idcategoria = ?")
@Where(clause="estado = 1")
public class Categorias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idcategoria;
    private String nombre;
    private String imagen;
    private Integer estado = 1;

    @OneToMany(
        mappedBy = "categoria",
        fetch = FetchType.LAZY)
    private List<Productos> productos = new ArrayList<>();

    public Categorias(){}

    public Categorias(Integer id){
        this.idcategoria = id;
    }

    public Integer getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(Integer idcategoria) {
        this.idcategoria = idcategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getImagen() {
        return imagen;
    }
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }


    public List<Productos> getProductos() {
        return productos;
    }

    public void setProductos(List<Productos> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "Categorias [idcategoria=" + idcategoria + ", nombre=" + nombre + ", imagen=" + imagen + ", estado="
                + estado + ", productos=" + productos + "]";
    }



}
