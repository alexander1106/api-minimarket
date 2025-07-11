package com.gadbacorp.api.entity.ventas;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gadbacorp.api.entity.administrable.Sucursales;

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
@Table(name="clientes")
@SQLDelete(sql="UPDATE clientes SET estado = 0 WHERE id_cliente = ?")
@Where(clause = "estado = 1")
public class Clientes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_cliente")
    private Integer idCliente; 
    private String tipoDocumento; 
    private String documento;
    private String nombre; 
    private String apellidos;
    private String  telefono; 
    private String email;
    private String direccion;
    private LocalDate fecha_registro;
    private Integer estado=1;

    public Clientes(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public Clientes() {
    }
@ManyToOne
@JoinColumn(name = "id_sucursal", referencedColumnName = "id_sucursal")
private Sucursales sucursal;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Ventas> ventas;
    

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Cotizaciones> cotizaciones;

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public LocalDate getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(LocalDate fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public List<Ventas> getVentas() {
        return ventas;
    }

    public void setVentas(List<Ventas> ventas) {
        this.ventas = ventas;
    }

    public List<Cotizaciones> getCotizaciones() {
        return cotizaciones;
    }

    public void setCotizaciones(List<Cotizaciones> cotizaciones) {
        this.cotizaciones = cotizaciones;
    }

    @Override
    public String toString() {
        return "Clientes [idCliente=" + idCliente + ", tipoDocumento=" + tipoDocumento + ", tipoContribuyente="
                 + ", documento=" + documento + ", nombre=" + nombre + ", telefono=" + telefono
                + ", email=" + email + ", direccion=" + direccion + ", fecha_registro=" + fecha_registro + ", estado="
                + estado + ", ventas=" + ventas + ", cotizaciones=" + cotizaciones + "]";
    }

    public Sucursales getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
    }
    
}
