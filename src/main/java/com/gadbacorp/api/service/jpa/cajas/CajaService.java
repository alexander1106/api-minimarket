package com.gadbacorp.api.service.jpa.cajas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.caja.Caja;
import com.gadbacorp.api.repository.caja.CajaRepository;
import com.gadbacorp.api.service.caja.ICajaService;
@Service
public class CajaService implements ICajaService {
    @Autowired
    private CajaRepository cajaRepository;
    @Override
    public Caja guardarCaja(Caja caja) {
        return cajaRepository.save(caja);
    }

    @Override
    public Caja editarCaja(Caja caja) {
        return cajaRepository.save(caja);
    }

    @Override
    public void eliminarCaja(Integer id) {
        cajaRepository.deleteById(id);
    }

    @Override
    public List<Caja> listarCaja() {
        return cajaRepository.findAll();
    }

    @Override
    public Optional<Caja> buscarCaja(Integer id) {
        return cajaRepository.findById(id);
    }
    
}
