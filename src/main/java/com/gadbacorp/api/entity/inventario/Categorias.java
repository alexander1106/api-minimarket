package com.gadbacorp.api.entity.inventario;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
    @Lob
  @Column(columnDefinition = "TEXT")       // o LONGTEXT si usas MySQL
  private String imagen;
    private Integer estado = 1;

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

    @Override
    public String toString() {
        return "Categorias [idcategoria=" + idcategoria + ", nombre=" + nombre + ", imagen=" + imagen + ", estado="
                + estado + "]";
    }

    

}