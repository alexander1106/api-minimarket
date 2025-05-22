package com.gadbacorp.api.entity.administrable;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

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