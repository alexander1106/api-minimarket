package com.gadbacorp.api.entity.ventas;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name="pagos")
public class Pagos {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer idPago;

    private String descripcion;
    private String fechaPago; 
    private String estadoPago; 


}
