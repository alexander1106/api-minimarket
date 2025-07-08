package com.gadbacorp.api.service.jpa.cajas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.caja.AperturaCaja;
import com.gadbacorp.api.repository.caja.AperturaCajaRepository;
import com.gadbacorp.api.service.caja.IAperturaCajaService;

@Service
public class AperturaCajaService implements IAperturaCajaService{
    @Autowired
    private AperturaCajaRepository aperturaCajaRepository;

    @Override
    public AperturaCaja guardarAperturaCaja(AperturaCaja aperturaCaja) {
       return aperturaCajaRepository.save(aperturaCaja);
    }

    @Override
    public AperturaCaja editarAperturaCaja(AperturaCaja aperturaCaja) {
        return aperturaCajaRepository.save(aperturaCaja);
    }

    @Override
    public List<AperturaCaja> listarAperturaCajas() {
        return aperturaCajaRepository.findAll();
    }

    @Override
    public Optional<AperturaCaja> buscarAperturaCaja(Integer id) {
        return aperturaCajaRepository.findById(id);
    }

    @Override
    public void eliminarAperturaCaja(Integer id) {
        aperturaCajaRepository.deleteById(id);
    }


    
}
