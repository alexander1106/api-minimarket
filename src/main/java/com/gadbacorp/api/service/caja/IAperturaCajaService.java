package com.gadbacorp.api.service.caja;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.caja.AperturaCaja;

public interface IAperturaCajaService {
    AperturaCaja guardarAperturaCaja(AperturaCaja aperturaCaja); 
    AperturaCaja editarAperturaCaja(AperturaCaja aperturaCaja); 
    List<AperturaCaja> listarAperturaCajas ();
    Optional<AperturaCaja> buscarAperturaCaja(Integer id); 
    void eliminarAperturaCaja(Integer id);
}
