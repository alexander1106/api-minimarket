package com.gadbacorp.api.service.compras;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.compras.CompraCompletaDTO;
import com.gadbacorp.api.entity.compras.Compras;
import com.gadbacorp.api.entity.compras.DetallesComprasDTO;

public interface IComprasService {
    Compras guardarCompra(Compras compra);
    Compras editarCompra(Compras compra);
    void eliminarCompra(Integer idCompra);
    Optional<Compras> buscarCompra(Integer idCompra);
    List<Compras> listarCompras();
    List<Compras> obtenerComprasPorProveedor(Integer idProveedor);
    
    // Nuevos métodos
    DetallesComprasDTO obtenerPreciosProducto(Integer idProducto);
    CompraCompletaDTO crearCompraCompleta(CompraCompletaDTO compraDTO);
}