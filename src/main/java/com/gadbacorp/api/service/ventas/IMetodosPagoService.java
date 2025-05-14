package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.MetodosPago;

public interface IMetodosPagoService {
    void guardarMetodoPago(MetodosPago metodoPago);
    List<MetodosPago> listarMetodosPago();
    Optional<MetodosPago> obtenerMetodoPago(Integer id);
    void eliminarMetodoPago(Integer id);
    void editarMetodosPago (MetodosPago metodoPago);
}
