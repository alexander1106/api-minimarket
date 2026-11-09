package com.gadbacorp.api.entity.administrable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gadbacorp.api.entity.compras.Proveedores;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "empresas")
@SQLDelete(sql = "UPDATE empresas SET estado=0 WHERE idempresa = ?")
@Where(clause = "estado=1")
public class Empresas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idempresa;
    private String razonsocial;
    private String ciudad;
    private String direccion;
    private String ruc;
    private LocalDate fechaRegistro;
    private String correo;
    @Lob
    @Column(name="logo", columnDefinition="LONGTEXT")
    private String logo;
    private Integer estado = 1;

    /** Nombre comercial (el que ve el cliente). razonsocial es la razon social registrada en SUNAT. */
    private String nombreComercial;
    private String propietario;
    private String departamento;
    private Boolean facturacionElectronica = false;
    @jakarta.persistence.Column(length = 1000)
    private String notas;
    private String telefono;
    private String distrito;

    @jakarta.persistence.ManyToOne
    @jakarta.persistence.JoinColumn(name = "id_plan")
    private Plan plan;

    /** MENSUAL o ANUAL. */
    private String ciclo = "MENSUAL";
    private LocalDate fechaVencimiento;
    private Boolean enPrueba = false;
    private Boolean sunatVerificado = false;
    /** Suspendida por el dueno de la plataforma: la empresa sigue existiendo (estado=1) pero no esta activa. */
    private Boolean suspendida = false;
    

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sucursales> sucursales = new ArrayList<>();

     @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
     @JsonManagedReference
    private List<Proveedores> proveedores = new ArrayList<>();

    public Empresas(Integer idempresa) {
        this.idempresa = idempresa;
    }

    public Empresas() {
    }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public Boolean getFacturacionElectronica() { return facturacionElectronica; }
    public void setFacturacionElectronica(Boolean facturacionElectronica) { this.facturacionElectronica = facturacionElectronica; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public String getPropietario() { return propietario; }
    public void setPropietario(String propietario) { this.propietario = propietario; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
    public String getCiclo() { return ciclo; }
    public void setCiclo(String ciclo) { this.ciclo = ciclo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public Boolean getEnPrueba() { return enPrueba; }
    public void setEnPrueba(Boolean enPrueba) { this.enPrueba = enPrueba; }
    public Boolean getSunatVerificado() { return sunatVerificado; }
    public void setSunatVerificado(Boolean sunatVerificado) { this.sunatVerificado = sunatVerificado; }
    public Boolean getSuspendida() { return suspendida; }
    public void setSuspendida(Boolean suspendida) { this.suspendida = suspendida; }

    public Integer getIdempresa() {
        return idempresa;
    }

    public void setIdempresa(Integer idempresa) {
        this.idempresa = idempresa;
    }

    public String getRazonsocial() {
        return razonsocial;
    }

    public void setRazonsocial(String razonsocial) {
        this.razonsocial = razonsocial;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

  
    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public List<Sucursales> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<Sucursales> sucursales) {
        this.sucursales = sucursales;
    }

    public List<Proveedores> getProveedores() {
        return proveedores;
    }

    public void setProveedores(List<Proveedores> proveedores) {
        this.proveedores = proveedores;
    }

    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }

}