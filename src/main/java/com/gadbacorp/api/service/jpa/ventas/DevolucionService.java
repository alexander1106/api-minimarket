package com.gadbacorp.api.service.jpa.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.ventas.Devoluciones;
import com.gadbacorp.api.repository.ventas.DevolucionesRepository;
import com.gadbacorp.api.service.ventas.IDevolucionesService;
@Service
public class DevolucionService implements IDevolucionesService {

    @Autowired
    private DevolucionesRepository devolucionesRepository;
    
    @Override
    public Devoluciones guardarDevoluciones(Devoluciones devolucion) {
        return devolucionesRepository.save(devolucion);
    }

    @Override
    public Devoluciones editarDevolucion(Devoluciones devolucion) {
        return devolucionesRepository.save(devolucion);
    }

    @Override
    public void elimmanrDevolucion(Integer idDevolucion) {
         devolucionesRepository.deleteById(idDevolucion);
    }

    @Override
    public List<Devoluciones> listarDevoluciones() {
        return devolucionesRepository.findAll();
    }

    @Override
    public Optional<Devoluciones> buscarDevolucion(Integer IdDevoluciones) {
        return devolucionesRepository.findById(IdDevoluciones);
   }
}
