package com.gadbacorp.api.service.jpa.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.ventas.DetallesCotizaciones;
import com.gadbacorp.api.repository.ventas.DetallesCotizacionesRepository;
import com.gadbacorp.api.service.ventas.IDetallesCotizacionesService;
@Service
public class DetallesCotizacionesService implements IDetallesCotizacionesService {

    @Autowired
    private DetallesCotizacionesRepository detallesCotizacionesRepository;
    @Override
    public DetallesCotizaciones guardarDetallesCotizaciones(DetallesCotizaciones detallesCotizaciones) {
        return detallesCotizacionesRepository.save(detallesCotizaciones);    
    }

    @Override
    public DetallesCotizaciones editarCotizaciones(DetallesCotizaciones detallesCotizaciones) {
        return detallesCotizacionesRepository.save(detallesCotizaciones);
    }

    @Override
    public void eliminarDetallesCotizaciones(Integer id) {
        detallesCotizacionesRepository.deleteById(id);
    }

    @Override
    public List<DetallesCotizaciones> listarDetallesCotizacioneses() {
        return detallesCotizacionesRepository.findAll();
    }

    @Override
    public Optional<DetallesCotizaciones> buscarDetallesCotizaciones(Integer id) {
        return detallesCotizacionesRepository.findById(id);
    }

    @Override
    public List<DetallesCotizaciones> buscarPorCotizacion(Integer idCotizaciones) {
    return detallesCotizacionesRepository.findByCotizaciones_IdCotizaciones(idCotizaciones);
    }
    
}
