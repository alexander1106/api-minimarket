package com.gadbacorp.api.service.security;

import com.gadbacorp.api.entity.security.Modulo;

import java.util.List;
import java.util.Optional;

public interface ModuloService {
    List<Modulo> listarTodos();
    Optional<Modulo> obtenerPorId(Long id);
    Modulo guardar(Modulo modulo);
    Modulo actualizar(Long id, Modulo modulo);
    void eliminar(Long id);
}
