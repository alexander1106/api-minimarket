package com.gadbacorp.api.service.jpa.ventas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.ventas.MetodosPago;
import com.gadbacorp.api.repository.ventas.MetodosPagoRepository;
import com.gadbacorp.api.repository.ventas.PagosRepository;
import com.gadbacorp.api.service.ventas.IMetodosPagoService;

@Service
public class MetodosPagoService implements  IMetodosPagoService{

    @Autowired
    private MetodosPagoRepository metodosPagoRepository;

    @Autowired
    private PagosRepository pagosRepository;

    @Override
    public MetodosPago guardarMetodoPago(MetodosPago metodoPago) {
        if (existeMetodoConNombre(metodoPago.getNombre())) {
            return null; // O puedes devolver un MetodosPago vacío, o manejar en el controlador
        }
        return metodosPagoRepository.save(metodoPago);
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
    public MetodosPago editarMetodosPago(MetodosPago metodoPago) {
        return metodosPagoRepository.save(metodoPago);
    }

    @Override
    public boolean existeMetodoConNombre(String nombre) {
        try {
            return metodosPagoRepository.findByNombre(nombre).isPresent();
        } catch (IncorrectResultSizeDataAccessException ex) {
            return true;
        }
    
}
}
