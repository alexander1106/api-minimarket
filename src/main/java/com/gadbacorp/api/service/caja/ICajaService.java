package com.gadbacorp.api.service.caja;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.caja.Caja;

public interface ICajaService {
    Caja guardarCaja(Caja caja);
    Caja editarCaja(Caja caja);
    void eliminarCaja(Integer id);
    List<Caja> listarCaja();
    Optional<Caja> buscarCaja(Integer id);
    
}
