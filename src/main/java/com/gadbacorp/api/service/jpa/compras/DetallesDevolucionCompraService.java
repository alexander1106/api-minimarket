package com.gadbacorp.api.service.jpa.compras;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.compras.DetallesDevolucionCompra;
import com.gadbacorp.api.repository.compras.DetallesDevolucionCompraRepository;
import com.gadbacorp.api.service.compras.IDetallesDevolucionCompraService;

@Service
public class DetallesDevolucionCompraService implements IDetallesDevolucionCompraService {

    @Autowired
    private DetallesDevolucionCompraRepository detallesRepository;

    @Override
    public DetallesDevolucionCompra guardarDetalle(DetallesDevolucionCompra detalle) {
        return detallesRepository.save(detalle);
    }

    @Override
    public DetallesDevolucionCompra actualizarDetalle(DetallesDevolucionCompra detalle) {
        return detallesRepository.save(detalle);
    }

    @Override
    public void eliminarDetalle(Integer idDetalle) {
        detallesRepository.deleteById(idDetalle);
    }

    @Override
    public Optional<DetallesDevolucionCompra> buscarDetallePorId(Integer idDetalle) {
        return detallesRepository.findById(idDetalle);
    }

    @Override
    public List<DetallesDevolucionCompra> buscarPorIdDevolucion(Integer idDevolucion) {
        return detallesRepository.findByDevolucionCompraIdDevolucionCompra(idDevolucion);
    }

    @Override
    public List<DetallesDevolucionCompra> listarTodosDetalles() {
        return detallesRepository.findAll();
    }
}