package com.gadbacorp.api.entity.ventas;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gadbacorp.api.entity.administrable.Sucursales;

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
@Table(name="metodos_pago")
@SQLDelete(sql="UPDATE metodos_pago SET estado = 0 WHERE id_metodo_pago = ?")
@Where(clause = "estado = 1")
public class MetodosPago {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id_metodo_pago")
    private Integer idMetodoPago; 
    private String nombre; 
    private Integer estado =1;
    @ManyToOne
    @JoinColumn(name = "id_sucursal", referencedColumnName = "id_sucursal")
    @JsonBackReference   // ⬅️ ESTA ANOTACIÓN CORTA EL CICLO
    private Sucursales sucursal;
        
    public Integer getIdMetodoPago() {
        return idMetodoPago;
    }
    public void setIdMetodoPago(Integer idMetodoPago) {
        this.idMetodoPago = idMetodoPago;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    @OneToMany(mappedBy = "metodosPago")
    private List<Pagos> pagos;

    public List<Pagos> getPagos() {
      return pagos;
    }
    public void setPagos(List<Pagos> pagos) {
      this.pagos = pagos;
    }
    public Sucursales getSucursal() {
        return sucursal;
    }
    public void setSucursal(Sucursales sucursal) {
        this.sucursal = sucursal;
    }
}
