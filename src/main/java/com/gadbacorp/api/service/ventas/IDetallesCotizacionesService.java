package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;

public interface  IDetallesCotizacionesService {
    DetallesCotizaciones guardarDetallesCotizaciones(DetallesCotizaciones detallesCotizaciones);
    DetallesCotizaciones editarCotizaciones(DetallesCotizaciones detallesCotizaciones);
    void eliminarDetallesCotizaciones(Integer id);
    List<DetallesCotizaciones> listarDetallesCotizacioneses();
    Optional<DetallesCotizaciones> buscarDetallesCotizaciones(Integer id);
    List<DetallesCotizaciones> buscarPorCotizacion(Integer idCotizaciones);

}
