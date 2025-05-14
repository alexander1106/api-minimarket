package com.gadbacorp.api.entity.inventario;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "almacenes")
@SQLDelete(sql = "UPDATE almacenes SET estado=0 WHERE idalmacen = ?")
@Where(clause="estado = 1")
public class Almacenes {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idalmacen;
    private String nombre;
    private String descripcion;
    private Integer estado = 1; 
    
    // ✅ Relación correcta con AlmacenProducto
    @OneToMany(mappedBy = "almacen", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"producto", "almacen"})
    private List<AlmacenProducto> almacenProductos;

    public Integer getIdalmacen() {
        return idalmacen;
    }

    public void setIdalmacen(Integer idalmacen) {
        this.idalmacen = idalmacen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public List<AlmacenProducto> getAlmacenProducto() {
        return almacenProductos;
    }

    public void setAlmacenProducto(List<AlmacenProducto> almacenProducto) {
        this.almacenProductos = almacenProducto;
    }

    @Override
    public String toString() {
        return "Almacenes [idalmacen=" + idalmacen + ", nombre=" + nombre + ", descripcion=" + descripcion + ", estado="
                + estado + ", almacenProducto=" + almacenProductos + "]";
    }

    
    
}
