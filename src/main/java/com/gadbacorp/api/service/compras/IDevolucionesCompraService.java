package com.gadbacorp.api.service.compras;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.compras.DevolucionesCompra;
import com.gadbacorp.api.entity.compras.DevolucionesCompraDTO;

public interface IDevolucionesCompraService {
    List<DevolucionesCompra> findAll();
    Optional<DevolucionesCompra> findById(Integer id);
    DevolucionesCompra save(DevolucionesCompraDTO devolucionesCompraDTO);
    DevolucionesCompra update(Integer id, DevolucionesCompraDTO devolucionesCompraDTO);
    void delete(Integer id);
}