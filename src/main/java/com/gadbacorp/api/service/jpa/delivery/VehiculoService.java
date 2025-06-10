package com.gadbacorp.api.service.jpa.delivery;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.delivery.Vehiculo;
import com.gadbacorp.api.repository.delivery.VehiculoRepository;
import com.gadbacorp.api.service.delivery.IVehiculoService;

@Service
public class VehiculoService implements IVehiculoService {

    @Autowired
    private VehiculoRepository vehiculoRepo;

    @Override
    public List<Vehiculo> buscarTodos() {
        return vehiculoRepo.findAll();
    }

    @Override
    public Vehiculo guardar(Vehiculo vehiculo) {
        return vehiculoRepo.save(vehiculo);
    }

    @Override
    public Vehiculo modificar(Vehiculo vehiculo) {
        return vehiculoRepo.save(vehiculo);
    }

    @Override
    public Optional<Vehiculo> buscarId(Integer id) {
        return vehiculoRepo.findById(id);
    }

    @Override
    public void eliminar(Integer id) {
        vehiculoRepo.deleteById(id);
    }
}
