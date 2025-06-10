package com.gadbacorp.api.service.seguridad;

import java.util.List;
import java.util.Optional;

import com.gadbacorp.api.entity.seguridad.Modulo;


public interface IModuloService {
    List<Modulo> listarTodos();
    Optional<Modulo> obtenerPorId(Long id);
    Modulo guardar(Modulo modulo);
    Modulo actualizar(Long id, Modulo modulo);
    void eliminar(Long id);
}