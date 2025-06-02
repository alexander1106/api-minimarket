package com.gadbacorp.api.service.jpa.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.ventas.DetallesDevolucion;
import com.gadbacorp.api.repository.ventas.DetallesDevolucionesRepository;
import com.gadbacorp.api.service.ventas.IDetallesDevoluciones;

@Service
public class DetallesDevolucionesService implements IDetallesDevoluciones{
    @Autowired
    private DetallesDevolucionesRepository detallesDevolucionesRepository;

    @Override
    public DetallesDevolucion guardarDetallesDevolucion(DetallesDevolucion detallesDevoluciones) {
        return detallesDevolucionesRepository.save(detallesDevoluciones);
    }

    @Override
    public DetallesDevolucion editarDetallesDevolucionn(DetallesDevolucion detallesDevoluciones) {
        return detallesDevolucionesRepository.save(detallesDevoluciones);
    }

    @Override
    public List<DetallesDevolucion> listarDetallesDevoluciones() {
        return detallesDevolucionesRepository.findAll();
    }

    @Override
    public Optional<DetallesDevolucion> buscarDetalleDevolucion(Integer id) {
        return detallesDevolucionesRepository.findById(id);
    }

    @Override
    public void eliminarDetallesCotizaciones(Integer id) {
        detallesDevolucionesRepository.deleteById(id);;    
    }
}
