package com.gadbacorp.api.service.caja;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.caja.ArqueoCaja;

public interface IArqueoCajaService {
    ArqueoCaja guardarArqueoCaja(ArqueoCaja arqueoCaja);
    ArqueoCaja editarArqueoCaja(ArqueoCaja arqueoCaja);
    List<ArqueoCaja> listarArqueo();
    void eliminarArqueo(Integer id);
    Optional<ArqueoCaja> bsucarArqueo(Integer id);
}
