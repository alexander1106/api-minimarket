package com.gadbacorp.api.repository.ventas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.Cotizaciones;

public interface  CotizacionesRepository extends JpaRepository<Cotizaciones, Integer> {

    List<Cotizaciones> findByCliente_Sucursal_IdSucursal(Integer idSucursal);

    List<Cotizaciones> findByCliente_Sucursal_IdSucursalAndEstadoCotizacion(Integer idSucursal, String estado);
    
}
