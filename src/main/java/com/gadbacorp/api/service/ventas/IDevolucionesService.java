package com.gadbacorp.api.service.ventas;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.ventas.Devoluciones;

public interface  IDevolucionesService {

    Devoluciones guardarDevoluciones(Devoluciones devolucion);
    Devoluciones editarDevolucion(Devoluciones devolucion);
    void elimmanrDevolucion(Integer idDevolucion);
    List<Devoluciones> listarDevoluciones();
    Optional<Devoluciones> buscarDevolucion(Integer IdDevoluciones);
    
}
