package com.gadbacorp.api.repository.ventas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.Cotizaciones;

public interface  CotizacionesRepository extends JpaRepository<Cotizaciones, Integer> {
    
}
