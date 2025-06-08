package com.gadbacorp.api.service.jpa.cajas;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gadbacorp.api.entity.caja.ArqueoCaja;
import com.gadbacorp.api.repository.caja.ArqueoCajaRepositoy;
import com.gadbacorp.api.service.caja.IArqueoCajaService;
@Service
public class ArqueoCajaService implements IArqueoCajaService {

    @Autowired
    private ArqueoCajaRepositoy arqueoCajaRepositoy;

    @Override
    public ArqueoCaja guardarArqueoCaja(ArqueoCaja arqueoCaja) {
        return arqueoCajaRepositoy.save(arqueoCaja);
    }

    @Override
    public ArqueoCaja editarArqueoCaja(ArqueoCaja arqueoCaja) {
        return arqueoCajaRepositoy.save(arqueoCaja);
    }

    @Override
    public List<ArqueoCaja> listarArqueo() {
        return arqueoCajaRepositoy.findAll();
    }

    @Override
    public void eliminarArqueo(Integer id) {
        arqueoCajaRepositoy.deleteById(id);
    }

    @Override
    public Optional<ArqueoCaja> bsucarArqueo(Integer id) {
        return arqueoCajaRepositoy.findById(id);
    }
    
}
