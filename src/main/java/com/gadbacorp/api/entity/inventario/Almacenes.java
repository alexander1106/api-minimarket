package com.gadbacorp.api.entity.inventario;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gadbacorp.api.entity.administrable.Sucursales;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    // ← NUEVO: referencia a la sucursal
    @ManyToOne
    @JoinColumn(name = "id_sucursal", nullable = false)
    private Sucursales sucursal;

    @OneToMany(mappedBy = "almacen",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<AlmacenProducto> almacenProductos = new ArrayList<>();

    public Almacenes() { }

    public Almacenes(Integer id) {
        this.idalmacen = id;
    }

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
    public Sucursales getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
    }

    public List<AlmacenProducto> getAlmacenProductos() {
        return almacenProductos;
    }

    public void setAlmacenProductos(List<AlmacenProducto> almacenProductos) {
        this.almacenProductos = almacenProductos;
    }
 
    @Override
    public String toString() {
        return "Almacenes [idalmacen=" + idalmacen + ", nombre=" + nombre + ", descripcion=" + descripcion + ", estado="
                + estado + "]";
    }


}
