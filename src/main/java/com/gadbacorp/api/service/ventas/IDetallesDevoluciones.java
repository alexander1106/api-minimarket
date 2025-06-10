package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.DetallesDevolucion;

public interface  IDetallesDevoluciones {
    DetallesDevolucion guardarDetallesDevolucion(DetallesDevolucion detallesDevoluciones);
    DetallesDevolucion editarDetallesDevolucionn(DetallesDevolucion  detallesDevoluciones);
    List<DetallesDevolucion> listarDetallesDevoluciones();
    Optional<DetallesDevolucion> buscarDetalleDevolucion(Integer id);
    void eliminarDetallesCotizaciones(Integer id);
    List<DetallesDevolucion> buscarPorIdDevolucion(Integer id);
    
}
