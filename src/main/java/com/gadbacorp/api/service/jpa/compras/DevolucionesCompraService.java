package com.gadbacorp.api.service.jpa.compras;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.DevolucionesCompra;
import com.gadbacorp.api.repository.compras.DevolucionesCompraRepository;
import com.gadbacorp.api.service.compras.IDevolucionesCompraService;

@Service
public class DevolucionesCompraService implements IDevolucionesCompraService {

    @Autowired
    private DevolucionesCompraRepository devolucionesRepository;

    @Override
    public DevolucionesCompra guardarDevolucion(DevolucionesCompra devolucion) {
        return devolucionesRepository.save(devolucion);
    }

    @Override
    public DevolucionesCompra editarDevolucion(DevolucionesCompra devolucion) {
        return devolucionesRepository.save(devolucion);
    }

    @Override
    public void eliminarDevolucion(Integer idDevolucion) {
        devolucionesRepository.deleteById(idDevolucion);
    }

    @Override
    public Optional<DevolucionesCompra> buscarDevolucion(Integer idDevolucion) {
        return devolucionesRepository.findById(idDevolucion);
    }

    @Override
    public List<DevolucionesCompra> listarDevoluciones() {
        return devolucionesRepository.findAll();
    }

    @Override
    public List<DevolucionesCompra> obtenerDevolucionesPorCompra(Integer idCompra) {
        return devolucionesRepository.findByCompraIdCompra(idCompra);
    }
}