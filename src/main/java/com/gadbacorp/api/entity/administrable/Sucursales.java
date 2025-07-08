package com.gadbacorp.api.entity.administrable;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.entity.empleados.Usuarios;
import com.gadbacorp.api.entity.inventario.Almacenes;
import com.gadbacorp.api.entity.ventas.Clientes;
import com.gadbacorp.api.entity.ventas.MetodosPago;

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
    private String contacto;
    private String direccion;
    private Integer estado = 1;
@OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
@JsonManagedReference
private List<MetodosPago> metodosPago;
    @ManyToOne
    @JoinColumn(name = "idempresa", referencedColumnName = "idempresa")
    @JsonIgnore
    private Empresas empresa;

    @OneToMany(mappedBy = "sucursales")
    @JsonIgnore
    private List<Caja> cajas;
    
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Usuarios> usuarios = new ArrayList<>();

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Almacenes> almacenes = new ArrayList<>();

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
@JsonIgnore
private List<Clientes> clientes = new ArrayList<>();

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    
    public Sucursales() {
    }

    public Sucursales(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
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

    public List<Almacenes> getAlmacenes() {
        return almacenes;
    }

    public void setAlmacenes(List<Almacenes> almacenes) {
        this.almacenes = almacenes;
    }

    @Override
    public String toString() {
        return "Sucursales [idSucursal=" + idSucursal + ", nombreSucursal=" + nombreSucursal + ", contacto=" + contacto
                + ", direccion=" + direccion + ", estado=" + estado + "]";
    }

    public List<Usuarios> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuarios> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Caja> getCajas() {
        return cajas;
    }

    public void setCajas(List<Caja> cajas) {
        this.cajas = cajas;
    }

    public List<MetodosPago> getMetodosPago() {
        return metodosPago;
    }

    public void setMetodosPago(List<MetodosPago> metodosPago) {
        this.metodosPago = metodosPago;
    }

    public List<Clientes> getClientes() {
        return clientes;
    }

    public void setClientes(List<Clientes> clientes) {
        this.clientes = clientes;
    }
}