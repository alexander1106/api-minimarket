package com.gadbacorp.api.service.jpa.delivery;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.delivery.EstadoMovimiento;
import com.gadbacorp.api.repository.delivery.EstadoMovimientoRepository;
import com.gadbacorp.api.service.delivery.IEstadoMovimientoService;

@Service
public class EstadoMovimientoService implements IEstadoMovimientoService{

    @Autowired
    private EstadoMovimientoRepository estadoRepo;

    @Override
    public List<EstadoMovimiento> buscarTodos() {
        return estadoRepo.findAll();
    }

    @Override
    public EstadoMovimiento guardar(EstadoMovimiento estadoMovimiento) {
        return estadoRepo.save(estadoMovimiento);
    }

    @Override
    public EstadoMovimiento modificar(EstadoMovimiento estadoMovimiento) {
        return estadoRepo.save(estadoMovimiento);
    }

    @Override
    public Optional<EstadoMovimiento> buscarId(Integer id) {
        return estadoRepo.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        estadoRepo.deleteById(id);
    }
}
