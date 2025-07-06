package com.gadbacorp.api.service.caja;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.caja.TransaccionesCaja;

public interface ITransaccionesCajaServices {
    TransaccionesCaja guardarTransaccion(TransaccionesCaja transaccionesCaja);
    TransaccionesCaja editarTransacciones(TransaccionesCaja transaccionesCaja);
    void eliminarTransacciones(Integer id);
    List<TransaccionesCaja> listarTransaccionesCajas();
    Optional<TransaccionesCaja> buscarTransaccion(Integer id); 
    List<TransaccionesCaja> listarPorAperturaCaja(Integer idAperturaCaja);
    List<TransaccionesCaja> obtenerPorApertura(Integer idApertura);


}
