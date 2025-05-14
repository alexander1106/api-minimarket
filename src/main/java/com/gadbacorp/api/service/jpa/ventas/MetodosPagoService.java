package com.gadbacorp.api.service.jpa.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.service.ventas.IMetodosPagoService;

public class MetodosPagoService implements  IMetodosPagoService{

    @Autowired
    private MetodosPagoRepository metodosPagoRepository;

    @Override
    public void guardarMetodoPago(MetodosPago metodoPago) {
        metodosPagoRepository.save(metodoPago);
    }

    @Override
    public List<MetodosPago> listarMetodosPago() {
        return metodosPagoRepository.findAll();
    }

    @Override
    public Optional<MetodosPago> obtenerMetodoPago(Integer id) {
        return metodosPagoRepository.findById(id);
    }

    @Override
    public void eliminarMetodoPago(Integer id) {
        metodosPagoRepository.deleteById(id);
    }

    @Override
    public void editarMetodosPago(MetodosPago metodoPago) {
        metodosPagoRepository.save(metodoPago);
    }

}
