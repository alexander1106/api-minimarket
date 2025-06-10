package com.gadbacorp.api.service.compras;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.compras.DetallesCompras;

public interface IDetallesComprasService {
List<DetallesCompras> listar();
    Optional<DetallesCompras> obtenerPorId(Integer id);
    DetallesCompras guardar(DetallesCompras detalleCompra);
    void eliminar(Integer id);
     List<DetallesCompras> listarPorCompra(Integer idCompra); // Nuevo métodoa
}