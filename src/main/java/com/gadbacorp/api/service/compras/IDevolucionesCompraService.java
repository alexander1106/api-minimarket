package com.gadbacorp.api.service.compras;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.compras.DevolucionesCompra;

public interface IDevolucionesCompraService {
    DevolucionesCompra guardarDevolucion(DevolucionesCompra devolucion);
    DevolucionesCompra editarDevolucion(DevolucionesCompra devolucion);
    void eliminarDevolucion(Integer idDevolucion);
    Optional<DevolucionesCompra> buscarDevolucion(Integer idDevolucion);
    List<DevolucionesCompra> listarDevoluciones();
    List<DevolucionesCompra> obtenerDevolucionesPorCompra(Integer idCompra);
}