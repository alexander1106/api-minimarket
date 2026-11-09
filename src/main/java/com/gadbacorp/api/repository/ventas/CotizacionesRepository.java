package com.gadbacorp.api.repository.ventas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.Cotizaciones;

public interface  CotizacionesRepository extends JpaRepository<Cotizaciones, Integer> {

    List<Cotizaciones> findByCliente_Sucursal_IdSucursal(Integer idSucursal);

    List<Cotizaciones> findByCliente_Sucursal_IdSucursalAndEstadoCotizacion(Integer idSucursal, String estado);
    

    @org.springframework.data.jpa.repository.Query("SELECT c.numeroCotizacion FROM Cotizaciones c WHERE c.numeroCotizacion LIKE :patron")
    List<String> listarNumerosPorPrefijo(@org.springframework.data.repository.query.Param("patron") String patron);
}
