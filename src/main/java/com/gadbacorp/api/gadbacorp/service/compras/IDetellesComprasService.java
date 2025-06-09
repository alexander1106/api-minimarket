package com.gadbacorp.api.gadbacorp.service.compras;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.gadbacorp.entity.compras.DetallesCompras;

public interface IDetellesComprasService {
List<DetallesCompras> listar();
    Optional<DetallesCompras> obtenerPorId(Integer id);
    DetallesCompras guardar(DetallesCompras detalleCompra);
    void eliminar(Integer id);
}