package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.Ventas;

public interface IVentasService {
    Ventas guardarVenta(Ventas venta); 
    Ventas editarVenta(Ventas venta); 
    void eliminarVenta(Integer idVenta);
    Optional<Ventas> buscarVenta(Integer idVenta);
    List<Ventas> listarVentaas();
    List<Ventas> obtenerVentasPorCliente(Integer clienteId);
}
