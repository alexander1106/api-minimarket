package com.gadbacorp.api.gadbacorp.service.security;

import com.gadbacorp.api.gadbacorp.entity.security.Rol;
import java.util.List;
import java.util.Optional;

public interface RolService {
    List<Rol> listarTodos();
    Optional<Rol> obtenerPorId(Long id);
    Optional<Rol> obtenerPorNombre(String nombre);
    Rol guardar(Rol rol);
    Rol actualizar(Long id, Rol rolActualizado);
    void eliminar(Long id);
}
