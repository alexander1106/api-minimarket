package com.gadbacorp.api.gadbacorp.entity.administrable;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gadbacorp.api.gadbacorp.entity.inventario.Almacenes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="sucursales")
@SQLDelete(sql = "UPDATE sucursales SET estado=0 WHERE id_sucursal = ?")
@Where(clause = "estado=1")
public class Sucursales {
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal")
    private Integer idSucursal;

    @Column(name = "nombre_sucursal")
    private String nombreSucursal;

    private Integer contacto;

    private String direccion;

    private Integer estado=1;

    @ManyToOne
    @JoinColumn(name = "idempresa")
    private Empresas empresa;

    // ← NUEVO: lista de almacenes de esta sucursal
    @OneToMany(mappedBy = "sucursal",cascade= CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Almacenes> almacenes = new ArrayList<>();

    // getters y setters existentes…

    public List<Almacenes> getAlmacenes() {
        return almacenes;
    }

    public void setAlmacenes(List<Almacenes> almacenes) {
        this.almacenes = almacenes;
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public Integer getContacto() {
        return contacto;
    }

    public void setContacto(Integer contacto) {
        this.contacto = contacto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Empresas getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresas empresa) {
        this.empresa = empresa;
    }

    @Override
    public String toString() {
        return "Sucursales [idSucursal=" + idSucursal + ", nombreSucursal=" + nombreSucursal + ", contacto=" + contacto
                + ", direccion=" + direccion + ", estado=" + estado + ", empresa=" + empresa + "]";
    }

}