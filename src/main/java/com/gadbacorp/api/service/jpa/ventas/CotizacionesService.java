package com.gadbacorp.api.service.jpa.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.ventas.Cotizaciones;
import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.repository.ventas.CotizacionesRepository;
import com.gadbacorp.api.repository.ventas.DetallesCotizacionesRepository;
import com.gadbacorp.api.service.ventas.ICotizacionesService;
@Service
public class CotizacionesService implements  ICotizacionesService {

    @Autowired
    private CotizacionesRepository cotizacionesRepository;
    @Autowired
    private DetallesCotizacionesRepository detallesCotizacionesRepository;

    @Override
    public Cotizaciones guardarCotizacion(Cotizaciones cotizacion) {
        return cotizacionesRepository.save(cotizacion);
    }

    @Override
    public Cotizaciones editarCotizaciones(Cotizaciones contizacion) {
        return cotizacionesRepository.save(contizacion);
    }

    @Override
    public void eliminarCotizaciones(Integer id) {
        cotizacionesRepository.deleteById(id);
    }

    @Override
    public List<Cotizaciones> listarCotizacioneses() {
        return  cotizacionesRepository.findAll();
    }

    @Override
    public Optional<Cotizaciones> buscarCotizacion(Integer id) {
        return cotizacionesRepository.findById(id);
    }

    @Override
    public List<DetallesCotizaciones> buscarDetallesPorCotizacion(Integer id) {
        return detallesCotizacionesRepository.findByCotizaciones_IdCotizaciones(id);

    }

    @Override
    public void eliminarDetalleCotizacion(Integer idDetallesCotizaciones) {
        detallesCotizacionesRepository.deleteById(idDetallesCotizaciones);
    }

    @Override
    public void guardarDetalleCotizacion(DetallesCotizaciones nuevoDetalle) {
        detallesCotizacionesRepository.save(nuevoDetalle);
    }
}
