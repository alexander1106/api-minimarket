package com.gadbacorp.api.entity.compras;

import java.sql.Date;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gadbacorp.api.entity.administrable.Empresas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "proveedores")
@SQLDelete(sql = "UPDATE proveedores SET estado=0 WHERE Id_Proveedor = ?")
@Where(clause = "estado=1")
public class Proveedores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Proveedor")
    private Integer idProveedor;

    private String nombre;
    private String ruc;
    private String regimen;
    private String telefono;
    private String gmail;
    private String direccion;
    private Date fecha_registro;
    private Integer estado = 1;

    @Column(name = "razon_social", length = 200)
    private String razonSocial;
    private String distrito;
    private String provincia;
    private String departamento;
    @Column(name = "contacto_nombre", length = 120)
    private String contactoNombre;
    @Column(name = "contacto_cargo", length = 80)
    private String contactoCargo;
    @Column(name = "telefono_secundario", length = 30)
    private String telefonoSecundario;
    @Column(name = "sitio_web", length = 200)
    private String sitioWeb;
    @Column(length = 120)
    private String rubro;
    @Column(name = "condicion_pago", length = 20)
    private String condicionPago;
    private Integer diasCredito;
    @Column(length = 3)
    private String moneda;
    @Column(name = "limite_credito", precision = 14, scale = 2)
    private java.math.BigDecimal limiteCredito;
    @Column(length = 80)
    private String banco;
    @Column(name = "numero_cuenta", length = 30)
    private String numeroCuenta;
    @Column(length = 20)
    private String cci;
    @Column(name = "estado_sunat", length = 30)
    private String estadoSunat;
    @Column(name = "condicion_sunat", length = 30)
    private String condicionSunat;
    @Column(length = 1000)
    private String notas;

    @ManyToOne
    @JoinColumn(name = "id_empresa", referencedColumnName = "idempresa")
    @JsonBackReference
    private Empresas empresa;

    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getContactoNombre() { return contactoNombre; }
    public void setContactoNombre(String contactoNombre) { this.contactoNombre = contactoNombre; }
    public String getContactoCargo() { return contactoCargo; }
    public void setContactoCargo(String contactoCargo) { this.contactoCargo = contactoCargo; }
    public String getTelefonoSecundario() { return telefonoSecundario; }
    public void setTelefonoSecundario(String telefonoSecundario) { this.telefonoSecundario = telefonoSecundario; }
    public String getSitioWeb() { return sitioWeb; }
    public void setSitioWeb(String sitioWeb) { this.sitioWeb = sitioWeb; }
    public String getRubro() { return rubro; }
    public void setRubro(String rubro) { this.rubro = rubro; }
    public String getCondicionPago() { return condicionPago; }
    public void setCondicionPago(String condicionPago) { this.condicionPago = condicionPago; }
    public Integer getDiasCredito() { return diasCredito; }
    public void setDiasCredito(Integer diasCredito) { this.diasCredito = diasCredito; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public java.math.BigDecimal getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(java.math.BigDecimal limiteCredito) { this.limiteCredito = limiteCredito; }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    public String getCci() { return cci; }
    public void setCci(String cci) { this.cci = cci; }
    public String getEstadoSunat() { return estadoSunat; }
    public void setEstadoSunat(String estadoSunat) { this.estadoSunat = estadoSunat; }
    public String getCondicionSunat() { return condicionSunat; }
    public void setCondicionSunat(String condicionSunat) { this.condicionSunat = condicionSunat; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Date getFecha_registro() {
        return fecha_registro;
    }

    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro;
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
        return "Proveedores [idProveedor=" + idProveedor + ", nombre=" + nombre + ", ruc=" + ruc + ", regimen="
                + regimen + ", telefono=" + telefono + ", gmail=" + gmail + ", direccion=" + direccion
                + ", fecha_registro=" + fecha_registro + ", estado=" + estado
                + ", empresa=" + (empresa != null ? empresa.getIdempresa() : "Sin Empresa") + "]";
    }
}
