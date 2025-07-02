package com.gadbacorp.api.service.compras;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.compras.DetallesDevolucionCompra;

public interface IDetallesDevolucionCompraService {
    DetallesDevolucionCompra guardarDetalle(DetallesDevolucionCompra detalle);
    DetallesDevolucionCompra actualizarDetalle(DetallesDevolucionCompra detalle);
    void eliminarDetalle(Integer idDetalle);
    Optional<DetallesDevolucionCompra> buscarDetallePorId(Integer idDetalle);
    List<DetallesDevolucionCompra> buscarPorIdDevolucion(Integer idDevolucion);
    List<DetallesDevolucionCompra> listarTodosDetalles();
}