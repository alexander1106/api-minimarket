package com.gadbacorp.api.repository.ventas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;

public interface DetallesCotizacionesRepository extends JpaRepository<DetallesCotizaciones, Integer>{
        List<DetallesCotizaciones> findByCotizaciones_IdCotizaciones(Integer idCotizaciones);

}

