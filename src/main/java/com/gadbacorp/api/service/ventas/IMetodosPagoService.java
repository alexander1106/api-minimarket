package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.MetodosPago;

public interface IMetodosPagoService {
    MetodosPago guardarMetodoPago(MetodosPago metodoPago);
    List<MetodosPago> listarMetodosPago();
    Optional<MetodosPago> obtenerMetodoPago(Integer id);
    void eliminarMetodoPago(Integer id);
    MetodosPago editarMetodosPago (MetodosPago metodoPago);
    boolean existeMetodoConNombre(String nombre);
    List<MetodosPago> listarPorSucursal(Integer idSucursal);

}
