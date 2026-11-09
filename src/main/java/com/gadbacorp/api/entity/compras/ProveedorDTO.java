package com.gadbacorp.api.entity.compras;

import java.sql.Date;

public class ProveedorDTO {

    private Integer idProveedor;
    private String nombre;
    private String ruc;
    private String regimen;
    private String telefono;
    private String gmail;
    private String direccion;
    private Date fecha_registro;
    private Integer estado;
    private Integer idEmpresa;
    private String empresaNombre;  // ✅ Incluido

    private String razonSocial;
    private String distrito;
    private String provincia;
    private String departamento;
    private String contactoNombre;
    private String contactoCargo;
    private String telefonoSecundario;
    private String sitioWeb;
    private String rubro;
    private String condicionPago;
    private Integer diasCredito;
    private String moneda;
    private java.math.BigDecimal limiteCredito;
    private String banco;
    private String numeroCuenta;
    private String cci;
    private String estadoSunat;
    private String condicionSunat;
    private String notas;

    // ---- calculados por el servidor (solo lectura: se ignoran si llegan en POST/PUT) ----
    /** NATURAL (RUC 10/15/16/17) o JURIDICA (RUC 20). */
    private String tipoPersona;
    private long totalCompras;
    private java.math.BigDecimal montoTotalComprado = java.math.BigDecimal.ZERO;
    /** Fecha (AAAA-MM-DD) de la ultima compra a este proveedor; null si nunca se le compro. */
    private String ultimaCompra;

    public ProveedorDTO() {}

    public Integer getIdProveedor() { return idProveedor; }
    public void setIdProveedor(Integer idProveedor) { this.idProveedor = idProveedor; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }

    public String getRegimen() { return regimen; }
    public void setRegimen(String regimen) { this.regimen = regimen; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getGmail() { return gmail; }
    public void setGmail(String gmail) { this.gmail = gmail; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Date getFecha_registro() { return fecha_registro; }
    public void setFecha_registro(Date fecha_registro) { this.fecha_registro = fecha_registro; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public Integer getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(Integer idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
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
    public String getTipoPersona() { return tipoPersona; }
    public void setTipoPersona(String tipoPersona) { this.tipoPersona = tipoPersona; }
    public long getTotalCompras() { return totalCompras; }
    public void setTotalCompras(long totalCompras) { this.totalCompras = totalCompras; }
    public java.math.BigDecimal getMontoTotalComprado() { return montoTotalComprado; }
    public void setMontoTotalComprado(java.math.BigDecimal montoTotalComprado) { this.montoTotalComprado = montoTotalComprado; }
    public String getUltimaCompra() { return ultimaCompra; }
    public void setUltimaCompra(String ultimaCompra) { this.ultimaCompra = ultimaCompra; }
}
