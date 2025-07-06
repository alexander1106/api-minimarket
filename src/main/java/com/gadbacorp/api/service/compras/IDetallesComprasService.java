package com.gadbacorp.api.service.compras;

import com.gadbacorp.api.entity.compras.DetallesCompras;
import java.util.List;
import java.util.Optional;

public interface IDetallesComprasService {
    DetallesCompras guardarDetalle(DetallesCompras detalle, Integer idCompra, Integer idproducto);
    DetallesCompras actualizarDetalle(DetallesCompras detalle);
    void eliminarDetalle(Integer idDetalle);
    Optional<DetallesCompras> buscarDetallePorId(Integer idDetalle);
    List<DetallesCompras> buscarPorIdCompra(Integer idCompra);
    List<DetallesCompras> listarTodosDetalles();
}