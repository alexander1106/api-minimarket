package com.gadbacorp.api.service.jpa.cajas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.caja.TransaccionesCaja;
import com.gadbacorp.api.repository.caja.TransaccionesCajaRepository;
import com.gadbacorp.api.service.caja.ITransaccionesCajaServices;
@Service
public class CajaTransaccionesService implements ITransaccionesCajaServices {

    @Autowired
    private TransaccionesCajaRepository transaccionesCajaRepository; 
    @Override
    public TransaccionesCaja guardarTransaccion(TransaccionesCaja transaccionesCaja) {
        return transaccionesCajaRepository.save(transaccionesCaja);
    }

    @Override
    public TransaccionesCaja editarTransacciones(TransaccionesCaja transaccionesCaja) {
        return transaccionesCajaRepository.save(transaccionesCaja);    
    }

    @Override
    public void eliminarTransacciones(Integer id) {    
        transaccionesCajaRepository.deleteById(id);
    }

    @Override
    public List<TransaccionesCaja> listarTransaccionesCajas() {
        return transaccionesCajaRepository.findAll();
    }

    @Override
    public Optional<TransaccionesCaja> buscarTransaccion(Integer id) {
        return transaccionesCajaRepository.findById(id);
    }
    
}
