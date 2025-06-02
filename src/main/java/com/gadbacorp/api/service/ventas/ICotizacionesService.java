package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.Cotizaciones;

public interface  ICotizacionesService {
    Cotizaciones guardarCotizacion(Cotizaciones cotizacion);
    Cotizaciones editarCotizaciones(Cotizaciones contizacion);
    void eliminarCotizaciones(Integer id);
    List<Cotizaciones> listarCotizacioneses();
    Optional<Cotizaciones> buscarCotizacion(Integer id);
}
